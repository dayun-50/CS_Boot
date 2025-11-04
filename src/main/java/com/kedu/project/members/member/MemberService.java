package com.kedu.project.members.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kedu.project.common.Encryptor;

import com.kedu.project.external.james.JamesAccountService;
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
    	
    try {
        int dbResult = dao.signup(dto);
        System.out.println("Member INSERT 성공: " + dbResult);
    	
        //  James 서버에 메일 계정 생성
        if (dbResult > 0) {
        	jamesAccountService.createMailAccount(dto.getEmail(), rawPassword);
        	System.out.println("James 계정 생성 성공");
        	
        }
        return dbResult;
    } catch (Exception e) {
        System.err.println("에러 발생 지점 확인:");
        e.printStackTrace();
        throw e;
    }

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
            
            return 0;
        }
        
        
        boolean jamesAuthSuccess = jamesAccountService.authenticateUser(dto.getEmail(), rawPassword);
       
        if (!jamesAuthSuccess) {
            // James 서버 인증 실패: DB에는 있지만 메일 서버 계정이 유효하지 않음
            // 메일 기능이 필수이므로, 예외를 발생시키거나 null 반환
            System.err.println("ERROR: 5. James 서버 계정 인증 실패! (평문 비밀번호 불일치 가능성 높음)"); 
            throw new RuntimeException("메일 서버 계정 인증에 실패했습니다. (관리자에게 문의하세요)");
            // return null; // 또는 null을 반환하여 로그인 실패 처리
        }
        System.out.println("INFO: 6. 모든 인증 성공. 로그인 처리 완료."); // 
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
    	String email = dto.getEmail();
        String rawPassword = dto.getPw();
    	
    	
        dto.setPw(Encryptor.encrypt(dto.getPw())); // 암호화
        
        int result = dao.gnewpw(dto);
        if (result > 0) {
            try {
                jamesAccountService.changePassword(email, rawPassword);
                
            } catch (Exception e) {
               
                e.printStackTrace();
                throw new RuntimeException("James 서버 비밀번호 변경 실패", e);
            }
        }
        
        
        return result;
    }

    // 마이페이지 출력
    public List<MemberDTO> mypage(String email) {
    	MemberDTO dto = new MemberDTO();
    	dto.setEmail(email);
        List<MemberDTO> list = dao.mypage(dto);
        String phone1 = list.get(0).getPhone().substring(3, 7); // 첫번째 전번
        String phone2 = list.get(0).getPhone().substring(7, 11); // 두번째 전번
        list.get(0).setPhone("010" + "-" + phone1 + "-" + phone2);
        return list;
    }
    
    

   
	// 마이페이지 수정
	public int updateMypage(MemberDTO dto, String email) {
		dto.setEmail(email);
		return dao.updateMypage(dto);
	}

	// -------------------- 주소록에 좀 뽑을게 --------------------------------
	// 이메일로 company_code 조회 - 주소록 추가시 팔요하여 넣음
	public String getCompanyCodeByEmail(String email) {
		MemberDTO member = dao.findByEmail(email);
		System.out.println(member);
		return member != null ? member.getCompany_code() : null;
	}

	
	public String getCompanyCodeEmail(String email) {
		System.out.println(dao.getCompanyCodeEmail(email));
		return dao.getCompanyCodeEmail(email);
	}



	
	

	// 부서
	public String getDeptCodeByEmail(String email) {
		// DAO를 통해 실제 부서 코드(DEPT_CODE)를 조회하도록 수정
		return dao.getDeptCodeByEmail(email);
	}



}
