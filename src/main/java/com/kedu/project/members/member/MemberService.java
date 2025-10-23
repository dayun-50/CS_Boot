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
	

	

    @Value("${james.local.domain}")
    private String localDomain;
    @Autowired
    private JamesAdminClient jamesAdminClient;

    // ----------------------------------------------------
    // 회원가입 (DB 저장 + James 계정 생성)
    // ----------------------------------------------------
    @Transactional
    public int signup(MemberDTO dto) {

        String rawPassword = dto.getPw();
        System.out.println("DEBUG: 1. 로그인 시도 이메일: " + dto.getEmail());
        // 1. James 계정 이름 생성 (헬퍼 메서드 호출)

        String jamesUsername = getJamesUsername(dto.getEmail());

        // 2. DB 저장을 위해 비밀번호 암호화 및 저장 (member 테이블)
        dto.setPw(Encryptor.encrypt(dto.getPw()));
        int dbResult = dao.signup(dto);

        // 3. DB 저장이 성공하면, James 서버에 메일 계정 생성
        if (dbResult > 0) {
            // James Admin Client 호출 (실패 시 RuntimeException 발생 -> DB 자동 롤백)
            jamesAdminClient.createMailAccount(jamesUsername, rawPassword);
        }

        return dbResult;
    }
    // 헬퍼 메서드: 이메일에서 ID를 추출하고 James 도메인 결합
    private String getJamesUsername(String fullEmail) {

        // 유효성 검사를 React에서 완료했다고 가정하고, @ 앞부분(ID)만 추출
        String userId = fullEmail.substring(0, fullEmail.indexOf('@'));

        // 최종 James 계정 이름 반환
        return userId + "@" + localDomain;
    }


 

    // 로그인
    public int login(MemberDTO dto) {

        // 1. 원본 비밀번호 확보 (IMAP/SMTP 사용을 위해 필요)
        String rawPassword = dto.getPw();
        System.out.println("DEBUG: 1. 로그인 시도 이메일: " + dto.getEmail()); // 💡 추가
        // 2. DB 인증을 위한 비밀번호 암호화 및 DAO 호출
        dto.setPw(Encryptor.encrypt(rawPassword)); // DB 비교를 위해 비밀번호 암호화
        int dbResult = dao.login(dto);

        // 3. DB 인증 실패 시 null 반환
        if (dbResult <= 0) {
            System.out.println("WARN: 2. DB 인증 실패. DAO 결과값: " + dbResult); // 💡 추가
            return 0;
        }
        System.out.println("INFO: 3. DB 인증 성공. James 서버 인증 시도."); // 💡 추가
        // 4. James 서버 계정 이름 생성 (예: user@test.com -> user@localhost.com)
        String jamesUsername = getJamesUsername(dto.getEmail());

        // 5. James 서버 인증 (확보된 원본 비밀번호 사용)
        System.out.println("DEBUG: 4. James 계정: " + jamesUsername + ", 평문 비밀번호 사용"); // 💡 추가
        boolean jamesAuthSuccess = jamesAdminClient.authenticateUser(jamesUsername, rawPassword);


        if (!jamesAuthSuccess) {
            // James 서버 인증 실패: DB에는 있지만 메일 서버 계정이 유효하지 않음
            // 메일 기능이 필수이므로, 예외를 발생시키거나 null 반환
            System.err.println("ERROR: 5. James 서버 계정 인증 실패! (평문 비밀번호 불일치 가능성 높음)"); // 💡 추가
            throw new RuntimeException("메일 서버 계정 인증에 실패했습니다. (관리자에게 문의하세요)");
            // return null; // 또는 null을 반환하여 로그인 실패 처리
        }
        System.out.println("INFO: 6. 모든 인증 성공. 로그인 처리 완료."); // 💡 추가
        // 6. 최종 성공: DB 인증 결과 반환
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
	
//	회사코드 연락처연동할 코드
	public String getCompanyCodeByEmail(String email) {
		return dao.getCompanyCodeByEmail(email);
	}

}
