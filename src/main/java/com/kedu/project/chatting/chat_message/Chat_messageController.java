package com.kedu.project.chatting.chat_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
	채팅방 메세지 기능 구현 controller
*/
@RequestMapping("")
@RestController
public class Chat_messageController {
	@Autowired
	private Chat_messageService Chat_messageService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
