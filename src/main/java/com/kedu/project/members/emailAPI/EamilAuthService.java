package com.kedu.project.members.emailAPI;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;

/*
 * 	회원가입 및 마이페이지 이메일 인증 API 전용 service
 * */
@Service
public class EamilAuthService {
	private static final long serialVersionUID = 1L;
	
	// 서버 전용 Naver 계정 (실제 발송용)
	@Value("${mail.from.email}") // 서버 전용 네이버 계정
    private String fromEmail; 
    @Value("${mail.app.password}") // 네이버 앱 비밀번호
    private String appPassword;
    String authCode = generateAuthCode(); // 발송할 인증 코드
	
    // 이메일 발송
    protected String doPost(String id)
            throws ServletException, IOException {
        String toEmail = URLDecoder.decode(id, StandardCharsets.UTF_8);; // 사용자가 입력한 이메일

        try {
            sendEmail(toEmail, authCode);
            return authCode;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 6자리 인증번호 생성
    private String generateAuthCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
    
    // 이메일 발송 준비
    private void sendEmail(String toEmail, String authCode) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        // 서버 계정 인증
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });
        
        String htmlContent = String.format("""
        		<p>회원가입 페이지로 다시 이동하여 아래에 받으신 인증번호를 입력해 주세요.</p>
        		<p>%s</p>
        		<p>* 이메일 수정시 다시 인증해야합니다. *</p>
        		""", authCode);

        // 메일 작성
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("CS 그룹웨어 회원가입 인증 확인", "UTF-8");
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        // 메일 발송
        Transport.send(message);
    }
}
