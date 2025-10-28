package com.kedu.project.emails.email;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.emails.MailSendRequestDTO;
import com.kedu.project.external.james.JamesAccountService;
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
    
    @Autowired
    private JamesAccountService jamesAccountService;
    

    // --- 💡 헬퍼 메서드 (토큰 분리) ---------------------------------------------------
    // 이 메서드만 남기고, 이전의 getLoggedInDbId, getRawJamesPassword 헬퍼는 모두 삭제되었습니다.

    /**
     * HTTP Request에서 JWT 토큰과 James Access Token을 분리하여 반환합니다.
     * @return String[0] = General Token, String[1] = James Access Token
     */
    private String[] splitAuthorizationToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 없거나 'Bearer ' 형식이 아닙니다.");
        }

        // "Bearer " 제거 후 전체 토큰 문자열 획득
        String fullToken = authorizationHeader.substring(7); 

        // "|||" 기준으로 단 한번만 분리
        String[] parts = fullToken.split("\\|\\|\\|", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("토큰 분리 문자열 '|||'이 누락되었거나 형식이 잘못되었습니다.");
        }

        return parts;
    }
    
 // --- 💡 /send (메일 발송) ---------------------------------------------------------

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
        HttpServletRequest request, 
        @RequestBody MailSendRequestDTO mailSendRequestDTO 
    ) {
        try {
            // 1. 💡 토큰 분리: General Token과 James Access Token 획득
            String[] tokenParts = splitAuthorizationToken(request);
            String generalToken = tokenParts[0]; 
            String jamesAccessToken = tokenParts[1];
            
            // 2. 💡 ID 및 비밀번호 획득 (JwtUtil 사용)
            // (a) 일반 토큰으로 DB ID 획득
            String senderDbId = jwtUtil.verifyToken(generalToken).getSubject(); 
            // (b) James Access Token으로 평문 비밀번호 획득
            String senderRawPassword = jwtUtil.getRawJamesPassword(jamesAccessToken);
            
            // 3. James 계정 아이디로 전환
            String senderJamesId = jamesAccountService.getJamesUsername(senderDbId); 
            
            // 4. 수신자 목록 파싱
            List<String> recipients = Arrays.stream(mailSendRequestDTO.getReceiverEmails().split(","))
                                            .map(String::trim)
                                            .toList();
            
            // 5. 발송 서비스 호출
            emailService.sendEmail(
                senderJamesId, senderRawPassword, recipients, 
                mailSendRequestDTO.getSubject(), mailSendRequestDTO.getContent()
            );

            return ResponseEntity.ok(Map.of("message", "메일 발송에 성공했습니다.", 
                                           "recipients", recipients.size() + "명"));

        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "메일 서버 인증 실패: James 계정 ID/PW 불일치"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "토큰 형식 오류: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "메일 발송 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    
 // --- 💡 /inbox (메일 수신) --------------------------------------------------------

    @GetMapping("/inbox") 
    public ResponseEntity<Map<String, Object>> getInboxMessages(HttpServletRequest request) {

        try {
            // 1. 💡 토큰 분리
            String[] tokenParts = splitAuthorizationToken(request);
            String generalToken = tokenParts[0]; 
            String jamesAccessToken = tokenParts[1];
            
            // 2. 💡 ID 및 비밀번호 획득
            String loggedInDbId = jwtUtil.verifyToken(generalToken).getSubject(); 
            String receiverRawPassword = jwtUtil.getRawJamesPassword(jamesAccessToken);
            
            // 3. James ID 생성 (IMAPS 연결에 사용)
            String receiverJamesId = jamesAccountService.getJamesUsername(loggedInDbId);
            
            // 4. 메일함 조회
            Message[] receivedMessages = emailService.getMailboxMessages(
                receiverJamesId, // 💡 James ID 사용
                receiverRawPassword, 
                "INBOX"
            );

            // 💡 DTO 변환 로직 추가 필요

            return ResponseEntity.ok(Map.of("status", "SUCCESS",
                                           "message", "메일함 조회 성공.",
                                           "received_count", receivedMessages.length));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "메일함 조회 오류: " + e.getMessage()));
        }
    }
    
    
    // --- 💡 /delete-all-emails (메일 삭제) ---------------------------------------------
    
    @DeleteMapping("/delete-all-emails") 
    public ResponseEntity<Map<String, Object>> deleteAllEmailsApi(HttpServletRequest request) {
        try {
            // 1. 💡 토큰 분리
            String[] tokenParts = splitAuthorizationToken(request);
            String generalToken = tokenParts[0]; 
            String jamesAccessToken = tokenParts[1];

            // 2. 💡 ID 및 비밀번호 획득
            String deleterDbId = jwtUtil.verifyToken(generalToken).getSubject();
            String deleterRawPassword = jwtUtil.getRawJamesPassword(jamesAccessToken);
            
            
            // 3. James 계정 ID로 전환
            String deleterJamesId = jamesAccountService.getJamesUsername(deleterDbId);
            
            // 4. 삭제 서비스 호출
            emailService.deleteAllEmails(deleterJamesId, deleterRawPassword);
            
            return ResponseEntity.ok(Map.of("message", "메일함 정리가 완료되었습니다.", "status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "메일 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
}