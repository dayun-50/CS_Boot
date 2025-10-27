package com.kedu.project.chatting.chat_room;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/*
	채팅방 생성 및 프로젝트 on off 기능 구현 Controller
*/
@RequestMapping("/chat")
@RestController
public class Chat_roomController {
	@Autowired
	private Chat_roomService Chat_roomService;
	

}
