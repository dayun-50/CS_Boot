package com.kedu.project.members.member;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		사원 회원가입 및 마이페이지 구현 DAO
 * */

@Repository
public class MemberDAO {
	@Autowired
	private SqlSession mybatis;
}
