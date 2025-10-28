package com.kedu.project.members.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kedu.project.common.Encryptor;
import com.kedu.project.external.james.JamesAccountService;



/*
 * 		사원 회원가입 및 마이페이지 구현 Service
 * */

@Service
public class MemberService {

	@Autowired
	private MemberDAO dao;
	// JamesAdminClient 주입

  
	@Autowired
    private JamesAccountService jamesAccountService;
    

    // ----------------------------------------------------
    // 회원가입 (DB 저장 + James 계정 생성)
    // ----------------------------------------------------
    @Transactional
    public int signup(MemberDTO dto) {
    	
    	String rawPassword = dto.getPw();
    	
    	//비밀번호 암호화 및 db 저장
    	dto.setPw(Encryptor.encrypt(dto.getPw()));
        int dbResult = dao.signup(dto);
    	
        // 4. DB 저장이 성공하면, James 서버에 메일 계정 생성
        if (dbResult > 0) {
        	jamesAccountService.createMailAccount(dto.getEmail(), rawPassword);
        }
        return dbResult;
	}
    
    

    // 로그인
    public int login(MemberDTO dto) {

        // 1. 원본 비밀번호 확보 (IMAP/SMTP 사용을 위해 필요)
        String rawPassword = dto.getPw();
       
        // 2. DB 인증을 위한 비밀번호 암호화 및 DAO 호출
        dto.setPw(Encryptor.encrypt(rawPassword)); // DB 비교를 위해 비밀번호 암호화
        int dbResult = dao.login(dto);

        // 3. DB 인증 실패 시 null 반환
        if (dbResult <= 0) {
            System.out.println("WARN: 2. DB 인증 실패. DAO 결과값: " + dbResult); // 💡 추가
            return 0;
        }
        System.out.println("INFO: 3. DB 인증 성공. James 서버 인증 시도."); // 💡 추가
        
        boolean jamesAuthSuccess = jamesAccountService.authenticateUser(dto.getEmail(), rawPassword);
       
        if (!jamesAuthSuccess) {
            // James 서버 인증 실패: DB에는 있지만 메일 서버 계정이 유효하지 않음
            // 메일 기능이 필수이므로, 예외를 발생시키거나 null 반환
            System.err.println("ERROR: 5. James 서버 계정 인증 실패! (평문 비밀번호 불일치 가능성 높음)"); // 💡 추가
            throw new RuntimeException("메일 서버 계정 인증에 실패했습니다. (관리자에게 문의하세요)");
            // return null; // 또는 null을 반환하여 로그인 실패 처리
        }
        System.out.println("INFO: 6. 모든 인증 성공. 로그인 처리 완료."); // 💡 추가
        // 7. 최종 성공: DB 인증 결과 반환
        // **주의:** 원본 비밀번호(rawPassword)는 이 메서드 외부로 DTO를 통해 전달되지 않습니다.
        // 별도의 세션/인증 로직에서 rawPassword를 관리해야 합니다.
        return dbResult;
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

	
////	회사코드 연락처연동할 코드
//	public String getCompanyCodeByEmail(String email) {
//		return dao.getCompanyCodeByEmail(email);
//	}


}
