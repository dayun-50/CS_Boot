package com.kedu.project.chatting.chat_room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.chatting.chat_member.Chat_memberDAO;
import com.kedu.project.chatting.chat_member.Chat_memberDTO;


/*
채팅방 생성 및 프로젝트 on off 기능 구현 Service
*/
@Service
public class Chat_roomService {
	@Autowired
	private Chat_roomDAO dao;
	
	@Autowired
	private Chat_memberDAO cMemberDao;
	
	// 방 생성자(매니저) 이메일 출력
	public Chat_roomDTO selectChatRoomManager(Chat_roomDTO dto) {
		return dao.selectChatRoomManager(dto);
	}
	
	// 채팅방 on/ off
	public int updateProjectOnOff(Chat_roomDTO dto) {
		return dao.updateProjectOnOff(dto);
	}
	
	// 채팅방 나가기
	public int outChat(Chat_memberDTO dto) {
		return cMemberDao.outChat(dto);
	}
}
