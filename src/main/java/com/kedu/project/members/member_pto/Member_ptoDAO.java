package com.kedu.project.members.member_pto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		남은 연차 관리 DAO
 * */

@Repository
public class Member_ptoDAO {
	@Autowired
	private SqlSession mybatis;
}
