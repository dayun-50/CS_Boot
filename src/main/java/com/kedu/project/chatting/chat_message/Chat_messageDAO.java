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
	
	// 파일 DB저장
	public Chat_messageDTO fileInsert(Chat_messageDTO dto) {
	    // 파일메시지 insert
	    mybatis.insert("Chat_message.fileInsert", dto);
	    // 2방금 삽입된 message_seq를 가져와서 전체 DTO 조회
	    return mybatis.selectOne("Chat_message.getLastInsertedMessage", dto.getChat_seq());
	}
	
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
	
	// 메세지 글자로 검색
	public List<Chat_messageDTO> serchByText(Chat_messageDTO dto){
		return mybatis.selectList("Chat_message.serchByText", dto);
	}
	
	// 메세지 날짜로 검색
	public List<Chat_messageDTO> serchByDate(Chat_messageDTO dto){
		return mybatis.selectList("Chat_message.serchByDate", dto);
	}
}
