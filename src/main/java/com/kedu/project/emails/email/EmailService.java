package com.kedu.project.emails.email;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kedu.project.members.member.MemberService;

import jakarta.mail.Authenticator;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private MemberService memberService; // 사용자 정보(ID/PW)를 조회하는 서비스

    // ----------------------------------------------------
    // 메일 서버 접속 정보 (James 서버 설정에 따라 조정 필요)
    // ----------------------------------------------------
 // EmailService.java (수정된 @Value)
    @Value("${james.host}")
    private String mailHost;

    @Value("${james.smtp.port}")
    private int smtpsPort; 

    @Value("${james.imap.port}")
    private int imapsPort;
    // EmailService.java에 추가
    public void deleteAllEmails(String email, String password) throws MessagingException {

        // 💡 993 포트를 사용하여 IMAPS 연결
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", this.mailHost);
        props.put("mail.imaps.port", imapsPort);
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.trust", mailHost);

        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect(this.mailHost, this.imapsPort, email, password);

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
    public void sendEmail(String fromEmail, String rawPassword, String toEmail, String subject, String content)
            throws MessagingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.host", this.mailHost);
        props.put("mail.smtps.port", this.smtpsPort);
        props.put("mail.smtps.ssl.trust", this.smtpsPort);
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
        transport.connect(this.mailHost, this.smtpsPort, fromEmail, rawPassword);
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
        props.put("mail.imaps.host", this.mailHost);
        props.put("mail.imaps.port", this.imapsPort);
        props.put("mail.imaps.ssl.enable", "true"); // SSL 비활성화 (imap.xml 설정 따름)
        props.put("mail.debug", "true");

        props.put("mail.imaps.ssl.trust", this.mailHost);

        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        // 2. James 서버 인증을 위해 메일 계정 ID/PW 사용
        store.connect(this.mailHost, this.imapsPort, email, rawPassword);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();

        // 연결 종료
        inbox.close(false);
        store.close();

        return messages;
    }
}