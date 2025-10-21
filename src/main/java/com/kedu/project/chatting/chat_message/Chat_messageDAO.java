package com.kedu.project.chatting.chat_message;

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
}
