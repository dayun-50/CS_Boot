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
	private String token;
	private int chat_seq;
	private static ObjectMapper mapper = new ObjectMapper();
	//	public static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
	private static Map<Integer, Set<Session>> roomSessions = Collections.synchronizedMap(new HashMap<>());
	private Chat_messageService cServ ;

	@OnOpen
	public void onConnection(Session session, EndpointConfig config) {
		if (cServ == null) {
			cServ = SpringProvider.ctx.getBean(Chat_messageService.class);
		}

		this.token = (String) config.getUserProperties().get("token");
		session.getUserProperties().put("token", token);
		String chatseq = (String) config.getUserProperties().get("chat_seq");
		this.chat_seq = Integer.parseInt(chatseq);
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
			System.out.println(member_eamil);
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
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("오류요 ㅋㅋ");
		}
	}

	@OnClose
	public void onClose(Session session) {
		// 방별 세션에서 제거
        for (Set<Session> sessions : roomSessions.values()) {
            sessions.remove(session);
        }
		System.out.println("클라이언트 종료: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable t) {
		 for (Set<Session> sessions : roomSessions.values()) {
	            sessions.remove(session);
	        }
		t.printStackTrace();
	}
}
