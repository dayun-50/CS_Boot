package com.kedu.project.external.james;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kedu.project.members.member.MemberDAO;

@Service
public class JamesAccountService {

    @Autowired
    private JamesAdminClient jamesAdminClient; // James 서버 통신 전담
    
    @Autowired
    private MemberDAO dao;
    
    @Value("${james.local.domain}")
    private String localDomain;

    /**
     * James 서버에 메일 계정을 생성하는 작업 (회원가입 시 호출)
     */
    public String getDomain(String email) {
        // DAO의 JOIN 쿼리를 호출하여 도메인 이름을 가져옵니다.
        String domain = dao.getDomainByEmail(email); 
        
        // 조회 실패 시 (NULL일 경우) 기본 도메인 사용
        return domain != null ? domain : localDomain;
    }
    
    /**
     * 💡 2. DB ID와 동적 도메인을 결합하여 James 서버 계정 ID를 생성합니다. (ID 변환)
     */
    public String getJamesUsername(String fullEmail) {
        String domain = getDomain(fullEmail); // 💡 본인의 도메인 조회
        String userId = fullEmail.substring(0, fullEmail.indexOf('@'));
        return userId + "@" + domain;
    }

    // --- 3. James 서버 API 호출 (CRUD) -----------------------------------------

    public void createMailAccount(String email, String rawPassword) {
        String jamesUsername = getJamesUsername(email); // 💡 ID 변환까지 내부에서 처리
        jamesAdminClient.createMailAccount(jamesUsername, rawPassword);
    }

    public boolean authenticateUser(String email, String rawPassword) {
        String jamesUsername = getJamesUsername(email); // 💡 ID 변환까지 내부에서 처리
        return jamesAdminClient.authenticateUser(jamesUsername, rawPassword);
    }
    
    
   
    
    // ... (setPassword, deleteMailbox 등의 메서드가 추가될 것입니다.) ...
}