package com.kedu.project.chatting.chat_message;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
	채팅방 메세지 기능 구현 DAO
*/
@Repository
public class Chat_messageDAO {
	@Autowired
	private SqlSession mybatis;
	
	// 메세지 DB저장
	public int messageInsert(Chat_messageDTO dto) {
		return mybatis.insert("Chat_message.messageInsert",dto);
	}
	
	// 방 seq에 따른 채팅내역 출력
	public List<Chat_messageDTO> getMessageBySeq(int chat_seq){
		return mybatis.selectList("Chat_message.getMessageBySeq", chat_seq);
	}
	
	// 채팅방 마지막 메세지 seq 출력
	public int lastMessageSeq(int chat_seq) {
		return mybatis.selectOne("Chat_message.lastMessageSeq", chat_seq);
	}
}
