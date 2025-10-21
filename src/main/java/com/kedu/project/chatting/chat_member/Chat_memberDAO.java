package com.kedu.project.chatting.chat_member;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
	채팅방 참여자 관리 DAO
*/
@Repository
public class Chat_memberDAO {
	@Autowired
	private SqlSession mybatis;
}
