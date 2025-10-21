package com.kedu.project.emails.email_box;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 *   	이메일함 기능 관련 DAO
 * */

@Repository
public class Email_boxDAO {
	@Autowired
	private SqlSession mybatis;
}
