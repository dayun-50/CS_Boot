package com.kedu.project.chatting.chat_message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.chatting.chat_member.Chat_memberDAO;
import com.kedu.project.chatting.chat_member.Chat_memberDTO;
import com.kedu.project.members.member.MemberDAO;


/*
	채팅방 메세지 기능 구현 Service
*/
@Service
public class Chat_messageService {
	@Autowired
	private Chat_messageDAO dao;
	
	@Autowired
	private Chat_memberDAO cMemberDao;
	
	@Autowired
	private MemberDAO memberDao;
	
	// 메세지 DB저장
	public int messageInsert(Chat_messageDTO dto) {
		return dao.messageInsert(dto);
	}
	
	// 방 seq에 따른 채팅내역 출력
	public List<Map<String, Object>> getMessageBySeq(int chat_seq){
		List<Chat_messageDTO> messages = dao.getMessageBySeq(chat_seq);
	    List<Map<String, Object>> messageList = new ArrayList<>();

	    for (Chat_messageDTO msg : messages) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("member_email", msg.getMember_email());
	        map.put("message", msg.getMessage());
	        map.put("message_at", msg.getMessage_at());
	        map.put("message_seq", msg.getMessage_seq());
	        String name = memberDao.selectMemberName(msg.getMember_email());
	        map.put("name", name);

	        messageList.add(map);
	    }
	    return messageList;
	}
	
	// 채팅방 마지막 메세지 시퀀스값 출력
	public int lastMessageSeq(int chatSeq) {
		return dao.lastMessageSeq(chatSeq);
	}
	
	// 채팅방 마지막 메세지 시퀀스 입력
	public int updateLastMessageSeq(String email, int messageSeq, int chatSeq) {
		Chat_memberDTO dto = new Chat_memberDTO();
		dto.setLast_message_seq(messageSeq);
		dto.setMember_email(email);
		dto.setChat_seq(chatSeq);
		
		return cMemberDao.updateLastMessageSeq(dto);
	}
	
	// 채팅방 멤버 뽑아오기
	public List<String> getMembersByRoomSeq(int chat_seq){
		return cMemberDao.getMembersByRoomSeq(chat_seq);
	}
	
	// 마지막으로 읽은 메세지 시퀀스 뽑기
	public int getLastMessageSeq(String email, int chatSeq) {
		return cMemberDao.getLastMessageSeq(email,chatSeq);
	}
}
