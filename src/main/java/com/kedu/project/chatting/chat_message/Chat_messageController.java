package com.kedu.project.chatting.chat_message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.members.member.MemberDTO;


/*
	채팅방 메세지 기능 구현 controller
*/
@RequestMapping("/chatMessage")
@RestController
public class Chat_messageController {
	@Autowired
	private Chat_messageService Chat_messageService;
	
//	@PostMapping("")
//	public ResponseEntity<Chat_messageDTO>
	
}
