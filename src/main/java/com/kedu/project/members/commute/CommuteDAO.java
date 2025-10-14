package com.kedu.project.members.commute;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		근태관리 기능 구현 DAO
 * */

@Repository
public class CommuteDAO {
	@Autowired
	private SqlSession mybatis;
}
