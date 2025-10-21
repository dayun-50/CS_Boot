package com.kedu.project.chatting.chat_room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
채팅방 생성 및 프로젝트 on off 기능 구현 Service
*/
@Service
public class Chat_roomService {
	@Autowired
	private Chat_roomDAO dao;
}
