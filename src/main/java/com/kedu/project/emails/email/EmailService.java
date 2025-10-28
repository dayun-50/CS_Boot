package com.kedu.project.emails.email;

import java.util.Properties;
import java.util.Collection; // Collection import 추가
import java.util.Arrays; // Arrays import 추가

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    // application.properties에서 @Value로 로드 (Final Configuration)
    @Value("${james.host}")
    private String mailHost;

    @Value("${james.smtp.port}")
    private int smtpsPort; 

    @Value("${james.imap.port}")
    private int imapsPort;
    
    // ----------------------------------------------------
    // 1. 메일 발송 및 Sent 폴더 저장 (핵심 기능)
    // ----------------------------------------------------

    /**
     * [POST /emails/send] SMTPS를 통해 메일을 발송하고, 'Sent' 폴더에 복사본을 저장합니다.
     * 💡 [수정] toEmails를 Collection<String>으로 변경하여 컴파일 오류 해결
     */
    public void sendEmail(String fromEmail, String rawPassword, Collection<String> toEmails, 
                          String subject, String content) throws Exception {
        
        // 1. 메시지 객체 생성 (전송과 저장을 위해 필요)
        Session smtpSession = getSmtpsSession(fromEmail, rawPassword);
        MimeMessage message = createMimeMessage(smtpSession, fromEmail, toEmails, subject, content);
        
        // 2. SMTPS를 통한 메일 발송
        sendSmtpMessage(message, fromEmail, rawPassword);

        // 3. IMAPS를 통한 'Sent' 폴더에 복사본 저장 (보낸 목록 확인을 위한 필수 작업)
        saveToSentFolder(message, fromEmail, rawPassword);
    }
    
    // --- 💡 Private 헬퍼 메서드: 발송 및 저장을 위한 세부 구현 --------------------

    // 💡 SMTPS 세션 획득
    private Session getSmtpsSession(String username, String password) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", mailHost);
        props.put("mail.smtps.port", smtpsPort);
        props.put("mail.smtps.auth", "true");
     // 💡 [추가] SSL 연결을 명시적으로 활성화
        props.put("mail.smtps.ssl.enable", "true"); 

        // 💡 [핵심 수정] 호스트 이름과 인증서 이름 불일치 검사를 무시합니다. 
        // 자체 서명된 인증서에서 가장 흔하게 발생하는 오류를 해결합니다.
        props.put("mail.smtps.ssl.checkserveridentity", "false"); 
        
        
        // 로컬 인증서 무시 설정 (기존 설정 유지)
        //props.put("mail.smtps.ssl.trust", mailHost); 
        props.put("mail.smtps.ssl.trust", "*");
        
        props.put("mail.debug", "true");
        
        
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    // 💡 메시지 객체 생성 (수신자 목록을 처리하도록 수정)
    private MimeMessage createMimeMessage(Session session, String from, Collection<String> to, String subject, String content) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        
     // 1. 💡 [수정] subject와 content가 null이 아닌지 확인하여 빈 문자열로 치환
        String safeSubject = (subject != null) ? subject : "";
        String safeContent = (content != null) ? content : "";
        // 💡 Collection<String>을 받아 String.join으로 쉼표로 구분된 String을 만들어 RecipientType.TO로 설정
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", to))); 
        message.setSubject(safeSubject, "UTF-8");
        message.setText(safeContent, "UTF-8");
        return message;
    }

    // 💡 SMTPS를 통한 실제 전송 로직
    private void sendSmtpMessage(MimeMessage message, String username, String password) throws MessagingException {
        Transport transport = message.getSession().getTransport("smtps");
        try {
            transport.connect(mailHost, smtpsPort, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            if (transport != null && transport.isConnected()) {
                transport.close();
            }
        }
    }

    // 💡 IMAPS를 통해 Sent 폴더에 메시지 복사본 저장 (보낸 목록 확인을 위한 필수 로직)
    private void saveToSentFolder(MimeMessage message, String username, String password) throws MessagingException {
        Store store = null;
        Folder sentFolder = null;
        try {
            Session imapsSession = getImapsSession();
            store = imapsSession.getStore("imaps");
            store.connect(mailHost, imapsPort, username, password); // 본인 계정으로 접속

            sentFolder = store.getFolder("Sent"); // "Sent" 폴더를 사용
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES); // 없으면 생성
            }
            sentFolder.open(Folder.READ_WRITE);
            
            // 메시지 복사본 저장
            sentFolder.appendMessages(new Message[]{message});

        } finally {
            if (sentFolder != null && sentFolder.isOpen()) {
                sentFolder.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
        }
    }

    // ----------------------------------------------------
    // 2. 메일 수신 기능 (IMAPS) - INBOX, Sent 폴더 조회
    // ----------------------------------------------------

    /**
     * [GET /emails/inbox, /emails/sent] 로그인 사용자 본인의 메일함을 조회합니다.
     * 💡 [수정] 메서드 이름을 getMailboxMessages로 변경하여 컨트롤러와 통일
     * @param folderName 조회할 폴더 이름 (예: "INBOX", "Sent")
     * @return Message[] 배열
     */
    public Message[] getMailboxMessages(String email, String rawPassword, String folderName) throws Exception {
        Store store = null;
        Folder folder = null;
        try {
            Session imapsSession = getImapsSession();
            store = imapsSession.getStore("imaps");
            store.connect(mailHost, imapsPort, email, rawPassword);
            
            // folderName 인자에 따라 INBOX 또는 Sent 폴더를 엽니다.
            folder = store.getFolder(folderName);
            if (!folder.exists()) {
                return new Message[0]; // 폴더가 없으면 빈 배열 반환
            }
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();

            return messages;

        } finally {
            // 연결 종료
            if (folder != null && folder.isOpen()) {
                folder.close(false); 
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
        }
    }

    // 💡 IMAPS 세션 획득 헬퍼
    private Session getImapsSession() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", mailHost);
        props.put("mail.imaps.port", imapsPort);
        //props.put("mail.imaps.ssl.enable", "true");
       // props.put("mail.imaps.ssl.trust", mailHost); // 로컬 인증서 무시
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.checkserveridentity", "false"); // 💡 [추가] 이름 검사 무시
        props.put("mail.imaps.ssl.trust", "*");                     // 💡 [추가] 모든 호스트 신뢰
        return Session.getDefaultInstance(props, null);
    }
    
    // ----------------------------------------------------
    // 3. 메일 삭제 기능 (IMAPS) - 기존 구현체
    // ----------------------------------------------------
    
    public void deleteAllEmails(String userEmail, String rawPassword) throws Exception {
        // ... (deleteAllEmails 로직은 getMailboxMessages 로직을 활용하여 구현 가능) ...
        // (Folder.READ_WRITE, message.setFlag(Flags.Flag.DELETED, true), inbox.expunge() 사용)
    }
}