package com.kedu.project.emails.email;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.emails.MailSendRequestDTO;
import com.kedu.project.members.member.MemberService;
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
    private MemberService memberService; // ID 변환용

    // --- 💡 헬퍼 메서드 (JWT 파싱) ---------------------------------------------------

    private String getLoggedInDbId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // 일반 웹 인증 토큰에서 Subject (DB ID) 추출
            return jwtUtil.verifyToken(token).getSubject(); 
        }
        throw new RuntimeException("인증 토큰이 누락되었습니다.");
    }
    
    private String getRawJamesPassword(HttpServletRequest request) {
        // James 전용 토큰은 일반적으로 커스텀 헤더로 받지만, 
        // 여기서는 편의를 위해 Authorization 헤더의 JWT를 사용한다고 가정합니다.
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // JWT 클레임에서 Base64 인코딩된 평문 비밀번호 복호화
            return jwtUtil.getRawJamesPassword(token); 
        }
        throw new RuntimeException("James 접근 토큰이 누락되었습니다.");
    }
    
 // --- 💡 /send (메일 발송) ---------------------------------------------------------

    /**
     * [POST] 메일 발송 - 로그인 사용자 본인이 다른 수신자에게 메일 발송
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
        HttpServletRequest request, 
        @RequestBody MailSendRequestDTO mailSendRequestDTO 
    ) {
        try {
            // 1. 발신자 (로그인 사용자) 정보 획득
            String senderDbId = getLoggedInDbId(request); 
            String senderRawPassword = getRawJamesPassword(request);
            String senderJamesId = memberService.getJamesUsername(senderDbId); // @localhost.com 형식
            
            // 2. 수신자 목록 파싱: 쉼표를 기준으로 분리하고 공백 제거
            List<String> recipients = Arrays.stream(mailSendRequestDTO.getReceiverEmails().split(","))
                                            .map(String::trim)
                                            .toList();
            
            // 3. 발송 서비스 호출 (발신자 본인의 계정으로 발송)
            for (String recipient : recipients) {
                 // 💡 EmailService.sendEmail 메서드는 한 번에 한 명의 수신자에게 보내도록 구현되었다고 가정합니다.
                 emailService.sendEmail(
                     senderJamesId, senderRawPassword, recipient, 
                     mailSendRequestDTO.getSubject(), mailSendRequestDTO.getContent()
                 );
            }

            return ResponseEntity.ok(Map.of("message", "메일 발송에 성공했습니다.", 
                                           "recipients", recipients.size() + "명"));

        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(401).body(Map.of("error", "메일 서버 인증 실패: James 계정 ID/PW 불일치"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "메일 발송 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    
 // --- 💡 /inbox (메일 수신) --------------------------------------------------------

    /**
     * [GET] 메일함 조회 (수신) - 로그인 사용자 본인의 메일함 조회
     */
    @GetMapping("/inbox") 
    public ResponseEntity<Map<String, Object>> getInboxMessages(HttpServletRequest request) {

        // 1. JWT에서 본인 정보 (수신자) 획득
        String loggedInDbId = getLoggedInDbId(request);
        String receiverRawPassword = getRawJamesPassword(request); 
        String receiverJamesId = memberService.getJamesUsername(loggedInDbId);

        try {
            // 2. 수신 테스트 (로그인 사용자 본인 메일함 조회)
            Message[] receivedMessages = emailService.receiveTestEmails(receiverJamesId, receiverRawPassword);

            // 💡 여기서는 메일 목록을 DTO로 변환하는 로직이 필요합니다. (생략)

            return ResponseEntity.ok(Map.of("status", "SUCCESS",
                                           "message", "메일함 조회 성공.",
                                           "received_count", receivedMessages.length));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "메일함 조회 오류: " + e.getMessage()));
        }
    }
    
    
    
    @DeleteMapping("/delete-all-emails") 
    public ResponseEntity<Map<String, Object>> deleteAllEmailsApi(HttpServletRequest request) {
        try {
            // 1. JWT에서 본인 정보 획득 (삭제할 메일함의 주인)
            String deleterDbId = getLoggedInDbId(request);
            String deleterRawPassword = getRawJamesPassword(request);
            String deleterJamesId = memberService.getJamesUsername(deleterDbId);
            
            // 2. 삭제 서비스 호출
            emailService.deleteAllEmails(deleterJamesId, deleterRawPassword);
            
            return ResponseEntity.ok(Map.of("message", "메일함 정리가 완료되었습니다.", "status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "메일 삭제 중 오류 발생"));
        }
    }
    
    
    
    
}