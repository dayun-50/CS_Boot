package com.kedu.project.chatting.chat_message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.members.member.MemberDAO;


/*
	채팅방 메세지 기능 구현 Service
*/
@Service
public class Chat_messageService {
	@Autowired
	private Chat_messageDAO dao;
	
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
}
