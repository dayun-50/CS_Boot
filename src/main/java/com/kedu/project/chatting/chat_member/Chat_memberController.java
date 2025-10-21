package com.kedu.project.chatting.chat_member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
	채팅방 참여자 관리 controller
*/
@RequestMapping("")
@RestController
public class Chat_memberController {
	@Autowired
	private Chat_memberService Chat_memberService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
