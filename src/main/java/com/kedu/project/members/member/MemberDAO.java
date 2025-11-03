package com.kedu.project.members.member;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kedu.project.chatting.chat_member.Chat_memberDTO;

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

	// 마이페이지 출력
	public List<MemberDTO> mypage(MemberDTO dto) {
		return mybatis.selectList("Member.selectByMypage", dto);
	}

	// 마이페이지 수정
	public int updateMypage(MemberDTO dto) {
		return mybatis.update("Member.updateMypage", dto);
	}

	// 부서원 정보 출력
	public List<MemberDTO> memberSearch(MemberDTO dto) {
		return mybatis.selectList("Member.memberSearch", dto);
	}

	// 부서명 출력
	public String selectDepartment(MemberDTO dto) {
		return mybatis.selectOne("Member.selectDepartment", dto);
	}

	// 사원 이름 출력
	public String selectMemberName(String email) {
		return mybatis.selectOne("Member.selectMemberName", email);
	}

	// 그룹웨어 사용중인 id인지 확인
	public int checkMember(String email) {
		return mybatis.selectOne("Member.checkMember", email);
	}

	// ---------------------------------------------------------------------
	// 전체 리스트 뽑아오기 : 입사일 기준 연차 지급 로딩용으로 필요함 -- 지원
	public List<MemberDTO> findAll() {
		return mybatis.selectList("Member.selectAll");
	}

	// 아이디로 dto 하나 뽑아오기 : 가입일자 별 연차 계산용-- 지원
	public MemberDTO findByEmail(String email) {
		return mybatis.selectOne("Member.findByEmail", email);
	}

	// ----------------------주소록
	public String getDeptCodeByEmail(String email) {
		// "Member.getDeptCodeByEmail" ID를 사용하여 쿼리 실행
		return mybatis.selectOne("Member.getDeptCodeByEmail", email);
	}
	
	public String selectMemberCompany(String email) {
		return mybatis.selectOne("Member.selectMemberCompany", email);
	}

}
