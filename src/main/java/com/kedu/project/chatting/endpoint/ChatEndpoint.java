package com.kedu.project.chatting.endpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kedu.project.chatting.chat_message.ChatFacadeService;
import com.kedu.project.chatting.chat_message.Chat_messageDTO;
import com.kedu.project.chatting.chat_message.Chat_messageService;
import com.kedu.project.config.SpringProvider;
import com.kedu.project.config.WebSocketConfigurator;
import com.kedu.project.file.FileConstants;
import com.kedu.project.file.FileDTO;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/*
	채팅 내용(메세지) UI에 뿌리는 로직용
 */
@ServerEndpoint(value ="/chatting", configurator = WebSocketConfigurator.class)
@Component
public class ChatEndpoint {

	private String token; // 토큰 받을 준비
	private int chat_seq; // 방 번호 받을 준비
	private static ObjectMapper mapper = new ObjectMapper(); // 받은 JSON dto로 전환 할 준비
	// 방 번호 및 세션에 유저 정보 넣어둘 준비
	private static Map<Integer, Set<Session>> roomSessions = Collections.synchronizedMap(new HashMap<>());
	// 채팅 서비스 Spring 에서 뽑아오기
	private Chat_messageService cServ ;
	//파일 추가용 레이어
	private ChatFacadeService chatFacade; 

	
	
	
	@OnOpen
	public void onConnection(Session session, EndpointConfig config) {
		
		if (cServ == null) { // 서비스 빈 뽑아오기
			cServ = SpringProvider.ctx.getBean(Chat_messageService.class);
		}
		if(chatFacade==null) {
			chatFacade =SpringProvider.ctx.getBean(ChatFacadeService.class);
		}


		// 유저에게서 받은 토큰 값 넣어두기
		this.token = (String) config.getUserProperties().get("token");
		// 받은 토큰 현재 클라이언트 정보(세션)에 저장
		session.getUserProperties().put("token", token); 
		// 전달받은 방 번호 저장
		String chatseq = (String) config.getUserProperties().get("chat_seq");
		this.chat_seq = Integer.parseInt(chatseq);
		// seq 방에 처음 유저가 접속한다면 새로운 Set<session>을 만든 후 추가,
		// 다음 유저가 들어온다면 이미 있는 Set에 추가 > 채팅방별로 참여 중인 클라이언트 관리 
		roomSessions.computeIfAbsent(chat_seq, k -> Collections.synchronizedSet(new java.util.HashSet<>())).add(session);

		System.out.println("클라이언트 접속: " + session.getId());

		// 지난 메세지 출력
		try {		
			//0.리턴값 맾으로 세팅
			Map<String, Object> result = new HashMap<>();
			
			
			//1. 채팅 전체 목록을 뽑아온다 :채팅방보내서
			List<Chat_messageDTO> messages = cServ.getMessageBySeq(chat_seq);
			// 2. 각 메시지에 연결된 파일 DTO 조회
			List<FileDTO> files = new ArrayList<>();
			for (Chat_messageDTO dto : messages) {
			    FileDTO temp = cServ.getFileByChatSeq(dto.getMessage_seq(),FileConstants.FC);
			    files.add(temp);
			}

			// 3. messages + files 병합
			List<Map<String, Object>> mergedList = new ArrayList<>();

			for (int i = 0; i < messages.size(); i++) {
			    Map<String, Object> entry = new HashMap<>();
			    entry.put("data", messages.get(i));
			    entry.put("fdata", files.get(i) != null ? files.get(i) : null);
			    mergedList.add(entry);
			}

			// 4. 최종 Map 생성
			Map<String, Object> finalResult = new HashMap<>();
			finalResult.put("type", "history");
			finalResult.put("each", mergedList);

		
			session.getBasicRemote().sendText(mapper.writeValueAsString(finalResult));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@OnMessage
	public void onMessage(String messageData, Session session) {
		System.out.println("가나다");
		try {
            Map<String, Object> jsonMap = mapper.readValue(messageData, Map.class);
            String type = (String) jsonMap.get("type");
            System.out.println(type);

            // 파일인경우
            if ("file".equals(type)) {
                int chat_seq = (int) jsonMap.get("chat_seq");//채팅, 파일이름 시퀀스 뽑아서
                String fileName = (String) jsonMap.get("file_name");

                session.getUserProperties().put("pending_chat_seq", chat_seq);// 세션에다가 임시저장
                session.getUserProperties().put("pending_file_name", fileName);

                System.out.println("파일 메타 수신됨: chat_seq=" + chat_seq + ", file_name=" + fileName);
                return;
            }
            
            
            //파일이 아니라 일반 채팅이라면
			Chat_messageDTO dto = mapper.readValue(messageData, Chat_messageDTO.class);
			// 토큰에서 id값 추출
			String token = (String) session.getUserProperties().get("token");
			String member_email = JWT.decode(token).getSubject();
			dto.setMember_email(member_email);
			// DB에 저장
			cServ.messageInsert(dto);
			// 방별 세션에만 메시지 전송
			Set<Session> clients = roomSessions.get(dto.getChat_seq());
			if (clients != null) {
				Map<String, Object> sendMap = new HashMap<>();
				sendMap.put("type", "chat");
				sendMap.put("data", dto);

				synchronized (clients) {
					for (Session client : clients) {
						client.getBasicRemote().sendText(mapper.writeValueAsString(sendMap));
					}
				}
			}

			// 다른 방에 있는 클라이언트들에게 새 메시지 알림 전송
			for (Map.Entry<Integer, Set<Session>> entry : roomSessions.entrySet()) {
				int roomSeq = entry.getKey();
				if (roomSeq != dto.getChat_seq()) { // 현재 메시지 방이 아니라면
					Set<Session> otherClients = entry.getValue();
					synchronized (otherClients) {
						for (Session otherClient : otherClients) {
							Map<String, Object> alert = new HashMap<>();
							alert.put("type", "alert");
							alert.put("chat_seq", dto.getChat_seq());
							otherClient.getBasicRemote().sendText(mapper.writeValueAsString(alert));
						}
					}
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("오류요 ㅋㅋ");
		}
	}
	
	
	//파일용 온 메세지
    @OnMessage(maxMessageSize = 10485760)
    public void handleBinary(Session session, byte[] data) throws IOException {
    	System.out.println("길이: " + data.length);
        Integer chatSeq = (Integer) session.getUserProperties().get("pending_chat_seq");
        String fileName = (String) session.getUserProperties().get("pending_file_name");
        String token = (String) session.getUserProperties().get("token");
        String member_email = JWT.decode(token).getSubject();

        if (chatSeq == null || fileName == null) {
            System.err.println("❌ 파일 수신 실패: 메타정보 없음");
            return;
        }
        
        //멀티파트 파일 형태로 만들기
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                fileName,
                "application/octet-stream",
                new ByteArrayInputStream(data)
        );
        MultipartFile[] files = { multipartFile };
        
        
        try {
        //db에 저장후 해당 dto 받아오기
        Map<String, Object> resultMap = chatFacade.fileInsert(chatSeq, member_email, files);
        Chat_messageDTO dto = (Chat_messageDTO) resultMap.get("dto");//메세지형 dto
        FileDTO fdto = (FileDTO) resultMap.get("fdto");//파일형 dto

        Set<Session> clients = roomSessions.get(dto.getChat_seq());// 방별 세션에만 메시지 전송
        
		if (clients != null) {
			Map<String, Object> sendMap = new HashMap<>();
			sendMap.put("type", "file");
			sendMap.put("data", dto);
			sendMap.put("fdata", fdto);
			
			
			synchronized (clients) {
				for (Session client : clients) {
					client.getBasicRemote().sendText(mapper.writeValueAsString(sendMap));
				}
			}
		}

		// 다른 방에 있는 클라이언트들에게 새 메시지 알림 전송

		for (Map.Entry<Integer, Set<Session>> entry : roomSessions.entrySet()) {
			int roomSeq = entry.getKey();
			if (roomSeq != dto.getChat_seq()) { // 현재 메시지 방이 아니라면
				Set<Session> otherClients = entry.getValue();
				synchronized (otherClients) {
					for (Session otherClient : otherClients) {
						Map<String, Object> alert = new HashMap<>();
						alert.put("type", "alert");
						alert.put("chat_seq", dto.getChat_seq());
						otherClient.getBasicRemote().sendText(mapper.writeValueAsString(alert));
					}
				}
			}
		}

	}catch(Exception e) {
		e.printStackTrace();
		System.out.println("오류요 ㅋㅋ");
	}
        
        
    }

	
	
	
	

	@OnClose
	public void onClose(Session session) {
		synchronized (roomSessions) {
			for (Map.Entry<Integer, Set<Session>> entry : roomSessions.entrySet()) {
				int chatSeq = entry.getKey();       // 현재 방의 chat_seq
				Set<Session> sessions = entry.getValue();
				if (sessions.contains(session)) {      // 세션이 이 방에 속해있다면
					String token = (String) session.getUserProperties().get("token");
					String member_eamil = JWT.decode(token).getSubject();
					int lastMessageSeq = cServ.lastMessageSeq(chatSeq);
					cServ.updateLastMessageSeq(member_eamil, lastMessageSeq, chatSeq);
					// 세션 제거
					sessions.remove(session);
				}
			}
		}
		System.out.println("클라이언트 종료: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable t) { // Throwable 은 우리가 쓰던 Exception 보다 부모 클래스(최상위 클래스)
		// Throwable 사용하는 이유는 웹소캣 표준 명세에 따르면 온에러에서는 두번째 파라미터가 반드시 Throwable 을 써야한데요...
		// Exception 써도 컴파일러는 통과하는데 정상적으로 호출되지 않거나 핸들러가 작동하지 않을수도있데용
		// 정상적인 채팅방 종료가 아닌 오류로 인한 사용종료 처리
		for (Set<Session> sessions : roomSessions.values()) {
			sessions.remove(session);
		}
		t.printStackTrace();
	}
}
