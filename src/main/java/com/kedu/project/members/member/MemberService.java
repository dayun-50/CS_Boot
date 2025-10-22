package com.kedu.project.members.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.common.Encryptor;
import com.kedu.project.members.member_pto.Member_ptoDAO;

/*
 * 		사원 회원가입 및 마이페이지 구현 Service
 * */

@Service
public class MemberService {
	@Autowired
	private MemberDAO dao;
	
	@Autowired
	private Member_ptoDAO daoPTO;
	
	// 회원가입
	public int signup(MemberDTO dto) {
		//dto.setPw(Encryptor.encrypt(dto.getPw()));  암호화
		
		// pto dao 통해서 초기 연차값 집어넣기
		daoPTO.insertInitPto(dto.getEmail()); 
		return dao.signup(dto);
	}
	
	// 로그인
	public int login(MemberDTO dto) {
		//dto.setPw(Encryptor.encrypt(dto.getPw()));  암호화
		return dao.login(dto);
	}
	
	// 비밀번호찾기(초반 이메일인증)
	public int findpw(MemberDTO dto) {
		return dao.findpw(dto);
	}
	
//	//이메일로 dto하나 가져오기
//	public MemberDTO findByEmail (String email) {
//		return dao.findByEmail(email);
//	}
	
	
}
