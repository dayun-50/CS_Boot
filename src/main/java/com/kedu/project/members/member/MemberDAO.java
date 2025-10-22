package com.kedu.project.members.member;

import java.util.List;

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
	
	//전체 리스트 뽑아오기 : 입사일 기준 연차 지급 로딩용으로 필요함 -- 지원
	public List<MemberDTO> findAll(){
		return mybatis.selectList("Member.selectAll");
	}
	
	//아이디로 dto 하나 뽑아오기 : 가입일자 별 연차 계산용-- 지원
	public MemberDTO findByEmail(String email) {
		return mybatis.selectOne("Member.findByEmail", email);		
	}
	
}
