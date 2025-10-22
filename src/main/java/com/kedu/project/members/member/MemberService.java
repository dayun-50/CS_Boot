package com.kedu.project.members.member;

import java.util.List;

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

	// 비밀번호 변경
	public int gnewpw(MemberDTO dto) {
		dao.gnewpw(dto);
		dto.setPw(Encryptor.encrypt(dto.getPw())); // 암호화
		return dao.gnewpw(dto);
	}
	
	// 마이페이지 출력
	public List<MemberDTO> mypage(MemberDTO dto){
		List<MemberDTO> list = dao.mypage(dto);
		String phone1 = list.get(0).getPhone().substring(3,7); // 첫번째 전번
		String phone2 = list.get(0).getPhone().substring(7,11); // 두번째 전번
		list.get(0).setPhone("010"+"-"+phone1+"-"+phone2);
		return list;
	}
	
	// 마이페이지 수정
	public int updateMypage(MemberDTO dto) {
		return dao.updateMypage(dto);
	}

}
