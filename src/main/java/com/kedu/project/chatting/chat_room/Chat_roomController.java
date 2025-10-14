package com.kedu.project.chatting.chat_room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.approval.ApprovalService;

/*
	채팅방 생성 및 프로젝트 on off 기능 구현 Controller
*/
@RequestMapping("")
@RestController
public class Chat_roomController {
	@Autowired
	private Chat_roomService Chat_roomService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
