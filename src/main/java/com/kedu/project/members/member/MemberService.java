package com.kedu.project.members.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.common.Encryptor;

/*
 * 		사원 회원가입 및 마이페이지 구현 Service
 * */

@Service
public class MemberService {
	@Autowired
	private MemberDAO dao;
	
	// 회원가입
	public int signup(MemberDTO dto) {
		dto.setPw(Encryptor.encrypt(dto.getPw())); // 암호화
		return dao.signup(dto);
	}
	
	// 로그인
	public int login(MemberDTO dto) {
		dto.setPw(Encryptor.encrypt(dto.getPw())); // 암호화
		return dao.login(dto);
	}
}
