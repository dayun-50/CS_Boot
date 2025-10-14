package com.kedu.project.chatting.chat_member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
	채팅방 참여자 관리 Service
*/
@Service
public class Chat_memberService {
	@Autowired
	private Chat_memberDAO dao;
}
