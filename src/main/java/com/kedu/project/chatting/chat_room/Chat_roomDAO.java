package com.kedu.project.chatting.chat_room;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
  	채팅방 생성 및 프로젝트 on off 기능 구현 DAO
  */
@Repository
public class Chat_roomDAO {
	@Autowired
	private SqlSession mybatis;
}
