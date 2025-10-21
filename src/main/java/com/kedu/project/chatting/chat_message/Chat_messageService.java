package com.kedu.project.chatting.chat_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
	채팅방 메세지 기능 구현 Service
*/
@Service
public class Chat_messageService {
	@Autowired
	private Chat_messageDAO dao;
}
