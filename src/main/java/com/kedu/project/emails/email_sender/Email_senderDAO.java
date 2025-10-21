package com.kedu.project.emails.email_sender;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
/*
	수신자 기능 관련 DAO
*/
@Repository
public class Email_senderDAO {
	@Autowired
	private SqlSession mybatis;
}
