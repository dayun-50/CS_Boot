package com.kedu.project.emails.email;

import com.kedu.project.members.member.MemberService;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private MemberService memberService; // 사용자 정보(ID/PW)를 조회하는 서비스

    // ----------------------------------------------------
    // 메일 서버 접속 정보 (James 서버 설정에 따라 조정 필요)
    // ----------------------------------------------------
    private static final String IMAP_HOST = "localhost"; // Docker 환경에 따라 James 컨테이너 이름일 수도 있음
    private static final int IMAPS_PORT = 993;
    private static final String SMTP_HOST = "localhost"; // Docker 환경에 따라 James 컨테이너 이름일 수도 있음
    private static final int SMTPS_PORT = 465;

    // EmailService.java에 추가
    public void deleteAllEmails(String email, String password) throws MessagingException {

        // 💡 993 포트를 사용하여 IMAPS 연결
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", IMAP_HOST);
        props.put("mail.imaps.port", IMAPS_PORT);
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.trust", IMAP_HOST);

        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect(IMAP_HOST, IMAPS_PORT, email, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE); // 💡 Read-Write 모드로 열어 삭제 권한 확보

        // 1. 모든 메시지를 가져와 삭제 플래그 설정
        Message[] messages = inbox.getMessages();
        int initialCount = messages.length;
        for (Message message : messages) {
            message.setFlag(Flags.Flag.DELETED, true);
        }

        // 2. EXPUNGE 명령을 통해 영구 삭제 명령 전송
        inbox.expunge();
        
        int finalCount = inbox.getMessages().length; 
        int deletedCount = initialCount - finalCount;

        inbox.close(false);
        store.close();

        System.out.println("INFO: IMAP을 통해 " + deletedCount + "개의 메시지를 삭제했습니다.");
    }

    /**
     * 특정 계정 정보로 James 서버에 접속하여 메일을 발송합니다.
     */
    public void sendTestEmail(String fromEmail, String rawPassword, String toEmail, String subject, String content)
            throws MessagingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.host", SMTP_HOST);
        props.put("mail.smtps.port", SMTPS_PORT);
        props.put("mail.smtps.ssl.trust", SMTP_HOST);
        props.put("mail.debug", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 2. James 서버 인증을 위해 메일 계정 ID/PW 사용
                return new PasswordAuthentication(fromEmail, rawPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject(subject);
        message.setText(content);

        // 💡 1. Transport 객체를 얻어와 연결하고 전송하는 코드를 직접 사용합니다.
        Transport transport = session.getTransport("smtps");
        transport.connect(SMTP_HOST, SMTPS_PORT, fromEmail, rawPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    /**
     * 특정 계정의 받은 편지함을 조회하여 메일 목록을 가져옵니다.
     */
    public Message[] receiveTestEmails(String email, String rawPassword) throws MessagingException {
        // 1. JWT에서 획득한 수신자 이메일을 사용하여 DB에서 비밀번호를 로드합니다.

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", IMAP_HOST);
        props.put("mail.imaps.port", IMAPS_PORT);
        props.put("mail.imaps.ssl.enable", "true"); // SSL 비활성화 (imap.xml 설정 따름)
        props.put("mail.debug", "true");

        props.put("mail.imaps.ssl.trust", IMAP_HOST);

        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        // 2. James 서버 인증을 위해 메일 계정 ID/PW 사용
        store.connect(IMAP_HOST, IMAPS_PORT, email, rawPassword);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();

        // 연결 종료
        inbox.close(false);
        store.close();

        return messages;
    }
}