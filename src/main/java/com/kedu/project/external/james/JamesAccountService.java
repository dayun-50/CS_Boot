  package com.kedu.project.external.james;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kedu.project.emails.email.EmailDAO;
import com.kedu.project.members.member.MemberDAO;

@Service
public class JamesAccountService {

    @Autowired
    private JamesAdminClient jamesAdminClient; // James 서버 통신 전담
    
    @Autowired
    private MemberDAO dao;
    
    @Autowired
    private EmailDAO emailDAO;
    
    @Value("${james.local.domain}")
    private String localDomain;

    /**
     * James 서버에 메일 계정을 생성하는 작업 (회원가입 시 호출)
     */
    public String getDomain(String email) {
        // DAO의 JOIN 쿼리를 호출하여 도메인 이름을 가져옵니다.
    	// member company join 회사코드 => 회사 도메인
        String domain = dao.getDomainByEmail(email); 
        
        // 조회 실패 시 (NULL일 경우) 기본 도메인 사용
        return domain != null ? domain : localDomain;
    }
    
    /**
     *  2. DB ID와 동적 도메인을 결합하여 James 서버 계정 ID를 생성합니다. (ID 변환)
     */
    public String getJamesUsername(String fullEmail) {
        String domain = getDomain(fullEmail); //  본인의 도메인 조회
        String userId = fullEmail.substring(0, fullEmail.indexOf('@'));
        return userId + "@" + domain; // 회사 도메인으로 메일 교체
    }

    // --- 3. James 서버 API 호출 (CRUD) -----------------------------------------

    public void createMailAccount(String email, String rawPassword) {
        String jamesUsername = getJamesUsername(email); //  기존 이메일 -> 회사 이메일 변환
        jamesAdminClient.createMailAccount(jamesUsername, rawPassword); // 회사 이메일 + 평문 암호
    }

    public boolean authenticateUser(String email, String rawPassword) {
        String jamesUsername = getJamesUsername(email); // 💡 ID 변환까지 내부에서 처리
        return jamesAdminClient.authenticateUser(jamesUsername, rawPassword);
    }
    
    
    
    ///////////////////////////////////////////// 필요없음
    
    
    
    @Transactional
    public void createDefaultMailboxes(String memberEmail) {
      
    	 try {
             String jamesUsername = getJamesUsername(memberEmail);
             
             // 1. James 서버에 INBOX, Sent 메일박스 생성
             jamesAdminClient.createMailbox(jamesUsername, "INBOX");
             System.out.println("James INBOX 생성: " + jamesUsername);
             
             jamesAdminClient.createMailbox(jamesUsername, "Sent");
             System.out.println("James Sent 생성: " + jamesUsername);
             
             // 2. Oracle DB에 메일박스 정보 저장
             int inboxResult = emailDAO.insertInbox(memberEmail);
             
             int sentResult = emailDAO.insertSent(memberEmail);
             
          // 3. 저장 확인
             int inboxCount = emailDAO.countByEmailAndType(memberEmail, "INBOX");
             int sentCount = emailDAO.countByEmailAndType(memberEmail, "Sent");
             
             if (inboxCount == 0 || sentCount == 0) {
                 throw new RuntimeException("DB에 메일박스 정보 저장 실패");
             }
             
             System.out.println("기본 메일박스 생성 완료: " + memberEmail);
             
         } catch (Exception e) {
             System.err.println("메일박스 생성 실패: " + memberEmail);
             e.printStackTrace();
             throw new RuntimeException("메일박스 생성 실패", e);
         }
     }
    	
    
   
    
    
    
    // ... (setPassword, deleteMailbox 등의 메서드가 추가될 것입니다.) ...
}