package com.kedu.project.members.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kedu.project.common.Encryptor;
import com.kedu.project.common.JamesAdminClient;

/*
 * 		사원 회원가입 및 마이페이지 구현 Service
 * */

@Service
public class MemberService {
	@Autowired
	private MemberDAO dao;
	// JamesAdminClient 주입

//	@Value("${james.local.domain}") 
//    private String localDomain;

	@Autowired
	private JamesAdminClient jamesAdminClient;

	// ----------------------------------------------------
	// 회원가입 (DB 저장 + James 계정 생성)
	// ----------------------------------------------------
	@Transactional
	public int signup(MemberDTO dto) {
		String rawPassword = dto.getPw();

		// 1. James 계정 이름 생성 (헬퍼 메서드 호출)
//        String jamesUsername = getJamesUsername(dto.getEmail()); 

		// 2. DB 저장을 위해 비밀번호 암호화 및 저장 (member 테이블)
		dto.setPw(Encryptor.encrypt(dto.getPw()));
		return dao.signup(dto);
//		int dbResult = dao.signup(dto); 
//        
//        // 3. DB 저장이 성공하면, James 서버에 메일 계정 생성
//        if (dbResult > 0) {
//            // James Admin Client 호출 (실패 시 RuntimeException 발생 -> DB 자동 롤백)
//			jamesAdminClient.createMailAccount(jamesUsername, rawPassword);
//        }
//        
//		return dbResult; 
	}

//    // 헬퍼 메서드: 이메일에서 ID를 추출하고 James 도메인 결합
//    private String getJamesUsername(String fullEmail) {
//        
//        // 유효성 검사를 React에서 완료했다고 가정하고, @ 앞부분(ID)만 추출
//        String userId = fullEmail.substring(0, fullEmail.indexOf('@'));
//        
//        // 최종 James 계정 이름 반환
//        return userId + "@" + localDomain;
//    }

	// 로그인
	public int login(MemberDTO dto) {
		dto.setPw(Encryptor.encrypt(dto.getPw())); // 암호화
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
	public List<MemberDTO> mypage(MemberDTO dto) {
		List<MemberDTO> list = dao.mypage(dto);
		String phone1 = list.get(0).getPhone().substring(3, 7); // 첫번째 전번
		String phone2 = list.get(0).getPhone().substring(7, 11); // 두번째 전번
		list.get(0).setPhone("010" + "-" + phone1 + "-" + phone2);
		return list;
	}

	// 마이페이지 수정
	public int updateMypage(MemberDTO dto) {
		return dao.updateMypage(dto);
	}

}
