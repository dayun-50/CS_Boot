package com.kedu.project.emails.email;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 *  이메일 기능 구현 DAO
 * */

@Repository
public class EmailDAO {
	@Autowired
	private SqlSession mybatis;
}
