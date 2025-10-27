package com.kedu.project.chatting.endpoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kedu.project.chatting.chat_message.Chat_messageDTO;
import com.kedu.project.chatting.chat_message.Chat_messageService;
import com.kedu.project.config.SpringProvider;
import com.kedu.project.config.WebSocketConfigurator;

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

	@OnOpen
	public void onConnection(Session session, EndpointConfig config) {
		if (cServ == null) { // 서비스 빈 뽑아오기
			cServ = SpringProvider.ctx.getBean(Chat_messageService.class);
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
			List<Map<String, Object>> messages = cServ.getMessageBySeq(chat_seq);
			Map<String, Object> result = new HashMap<>();
			result.put("type", "history");
			result.put("messages", messages);

			session.getBasicRemote().sendText(mapper.writeValueAsString(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@OnMessage
	public void onMessage(String messageData, Session session) {
		try {
			Chat_messageDTO dto = mapper.readValue(messageData, Chat_messageDTO.class);
			// 토큰에서 id값 추출
			String token = (String) session.getUserProperties().get("token");
			String member_eamil = JWT.decode(token).getSubject();
			dto.setMember_email(member_eamil);
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
            
            // 현재 안보고있는 방에 새로운 데이터(채팅) 발생시 알람 기능
            for(Map.Entry<Integer, Set<Session>> entry : roomSessions.entrySet()) {
            	// 방 번호 뽑아오기
            	int chatseq = entry.getKey();
            	// 현재 그 해당 채팅방에 존재하지 않을시
            	if(chatseq != dto.getChat_seq()) {
            		// 채팅방에 존재하지 않는 클라이언트 정복값
            		Set<Session> otherClients  = entry.getValue();
            		synchronized (otherClients) {
            			// 새로운 데이터(채팅)가 들어온 채팅방에 알람타입 send
						for(Session otherClient : otherClients) {
							Map<String, Object> map = new HashMap<>();
							map.put("type", "alert");
							map.put("chat_seq", chatseq);
							otherClient.getBasicRemote().sendText(mapper.writeValueAsString(map));
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
		// 모든 채팅방 반복문을 실행하며 현제 끊어진 세션을 찾아 제거
        for (Set<Session> sessions : roomSessions.values()) {
            sessions.remove(session);
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
