package com.kedu.project.emails.email;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.security.JwtUtil;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String TEST_RECEIVER = "quickly3899@localhost.com";
    private static final String TEST_PASSWORD = "test1234";

    @GetMapping("/test-inbox")
    public ResponseEntity<Map<String, Object>> testReceiveInbox(HttpServletRequest request) {

        // 1. JWT를 통해 현재 사용자의 이메일(ID)을 획득해야 합니다.
        // (JwtAuthenticationFilter에서 이 정보를 request 속성에 저장했다고 가정하거나,
        // 테스트를 위해 JWT 페이로드에서 직접 추출한다고 가정합니다.)
        String userEmail = TEST_RECEIVER; // 임시: 실제로는 JWT에서 추출해야 함

        // 2. 💡 James 서버 접속에 필요한 '평문 비밀번호'를 획득해야 합니다.
        // (로그인 시 세션에 저장했거나, JWT 페이로드에 포함되어 있다고 가정합니다.)
        // 현재 구조로는 획득이 불가능하므로, 아래 코드는 **임시 테스트용**으로만 사용하세요.

        Map<String, Object> response = new HashMap<>();

        try {
            // 3. 송신 테스트 (보내는 사람은 임시로 다른 계정 사용)
            emailService.sendTestEmail("user04@localhost.com", TEST_PASSWORD, TEST_RECEIVER,
                    "JWT 기반 송수신 테스트", "테스트 성공 시 이 메일이 도착합니다.");

            // 4. 수신 테스트 (방금 보낸 메일 조회)
            // 💡 EmailService의 메서드를 평문 비밀번호를 받는 형식으로 변경하면 더 좋습니다.
            // 현재는 MemberService에 의존하므로, 그대로 진행합니다.

            Message[] receivedMessages = emailService.receiveTestEmails(userEmail, TEST_PASSWORD);

            // ... (메일 목록 처리 로직은 생략) ...

            response.put("status", "SUCCESS");
            response.put("message", "송수신 테스트 완료. 메일 발송 및 수신 성공.");
            response.put("received_count", receivedMessages.length);

            return ResponseEntity.ok(response);

        } catch (AuthenticationFailedException e) {
            response.put("status", "AUTH_FAILED");
            response.put("error", "메일 서버 인증 실패: James 계정 ID/PW 불일치. **현재 하드코딩된 비밀번호를 확인하세요.**");
            // ... (로그 출력) ...
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", "시스템 오류: " + e.getMessage());
            // ... (로그 출력) ...
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping("/delete-all-emails") // DELETE 메서드 사용 권장
    public ResponseEntity<Map<String, Object>> deleteAllEmailsApi() {
        try {
            // 💡 삭제할 계정과 비밀번호를 전달합니다.
            emailService.deleteAllEmails(TEST_RECEIVER, TEST_PASSWORD);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "메일함 정리가 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ... (오류 처리) ...
            return ResponseEntity.status(500).body(Map.of("error", "메일 삭제 중 오류 발생"));
        }
    }
    
    
    
    
}