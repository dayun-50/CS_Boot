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
	
	// 회원가입
	public int signup(MemberDTO dto) { 
		return mybatis.insert("Member.insert", dto);
	}
	
	// 로그인
	public int login(MemberDTO dto) {
		return mybatis.selectOne("Member.selectByLogin", dto);
	}
	
	// 비밀번호찾기(초반 이메일인증)
	public int findpw(MemberDTO dto) {
		return mybatis.selectOne("Member.selectById", dto);
	}
	
	// 비밀번호 변경
	public int gnewpw(MemberDTO dto) {
		return mybatis.update("Member.updateByGnewpw", dto);
	}
}
