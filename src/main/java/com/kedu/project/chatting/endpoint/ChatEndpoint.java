package com.kedu.project.chatting.endpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static Map<Session,String> users = Collections.synchronizedMap(new HashMap<>());
	private Chat_messageService cServ ;

	@OnOpen
	public void onConnection(Session session, EndpointConfig config) {
		if (cServ == null) {
			cServ = SpringProvider.ctx.getBean(Chat_messageService.class);
		}

		this.token = (String) config.getUserProperties().get("token");
		String chatseq = (String) config.getUserProperties().get("chat_seq");
		this.chat_seq = Integer.parseInt(chatseq);
		users.put(session, token);
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
	public void onMessage(String messageData) {
		try {
			Chat_messageDTO dto = mapper.readValue(messageData, Chat_messageDTO.class);
			// 토큰에서 id값 추출
			String member_eamil = JWT.decode(this.token).getSubject();
			dto.setMember_email(member_eamil);
			// DB에 저장
			cServ.messageInsert(dto);
			synchronized (token) {
				// 클라이언트가 현재 있는 방 seq와 같은 방의 메세지만 출력하게끔
				for (Session client : users.keySet()) {
					if(dto.getChat_seq() == chat_seq) {
						client.getBasicRemote().sendText(mapper.writeValueAsString(dto));
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
		users.remove(session);
		System.out.println("클라이언트 종료: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable t) {
		users.remove(session);
		t.printStackTrace();
	}
}
