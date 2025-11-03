package com.kedu.project.emails.email;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.util.List;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kedu.project.emails.james.JamesEmailDAO;
import com.kedu.project.emails.james.JamesEmailDTO;

import jakarta.mail.Authenticator;
import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

@Service
public class EmailService {	

	@Autowired
	private OracleEmailDAO oracleDao;
	
	@Autowired
	private JamesEmailDAO jamesDao;

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
	 *  [수정] toEmails를 Collection<String>으로 변경하여 컴파일 오류 해결
	 */
	public void sendEmail(String fromEmail, String rawPassword, Collection<String> toEmails, 
			String subject, String content) throws Exception {
		System.out.println("sendemail 들어옴");
		// 1. 메시지 객체 생성 (전송과 저장을 위해 필요)
		
		Session smtpSession = getSmtpsSession(fromEmail, rawPassword);
		MimeMessage message = createMimeMessage(smtpSession, fromEmail, toEmails, subject, content);
		System.out.println("sendemail 객체 생성함");
		// 2. SMTPS를 통한 메일 발송
		sendSmtpMessage(message, fromEmail, rawPassword);
		System.out.println("메일발송하고 난 후");
		// 3. IMAPS를 통한 'Sent' 폴더 저장 + Oracle 동기화
		saveToSentFolder(message, fromEmail, rawPassword, toEmails);
	}

	// ---  Private 헬퍼 메서드: 발송 및 저장을 위한 세부 구현 --------------------

	//  SMTPS 세션 획득
	private Session getSmtpsSession(String username, String password) {
		System.out.println("왜 안떠요!?!?!?!?!??!");
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", mailHost);
		props.put("mail.smtps.port", smtpsPort);
		props.put("mail.smtps.auth", "true");
		//  [추가] SSL 연결을 명시적으로 활성화
		props.put("mail.smtps.ssl.enable", "true");
		
		

		//  [핵심 수정] 호스트 이름과 인증서 이름 불일치 검사를 무시합니다. 
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

	private MimeMessage createMimeMessage(Session session, String from, Collection<String> toCollection, String subject, String content) throws MessagingException {
	    MimeMessage message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(from));

	    // subject와 content null 처리
	    String safeSubject = (subject != null) ? subject : "";
	    String safeContent = (content != null) ? content : "";

	    // toCollection이 null이면 빈 리스트로 처리
	    Collection<String> safeTo = (toCollection != null) ? toCollection : Collections.emptyList();

	    // 공백 제거 및 빈 문자열 제외
	    List<String> filteredTo = safeTo.stream()
	                                    .map(String::trim)
	                                    .filter(email -> !email.isEmpty())
	                                    .toList();

	    // 수신자가 없으면 발송 안 함
	    if (filteredTo.isEmpty()) {
	        System.out.println("메일 수신자가 없습니다. 발송을 건너뜁니다.");
	        return null; // 또는 필요에 따라 예외 처리
	    }

	    // RecipientType.TO 세팅
	    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", filteredTo)));

	    message.setSubject(safeSubject, "UTF-8");
	    message.setText(safeContent, "UTF-8");
	    
	    return message;
	}
	//  SMTPS를 통한 실제 전송 로직
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

	//  IMAPS를 통한 'Sent' 폴더 저장 + Oracle 동기화
	private void saveToSentFolder(MimeMessage message, String username, String password , Collection<String> toEmails) throws MessagingException {
		Store store = null;
		Folder sentFolder = null;
		try {
			// James 서버 작업 (IMAPS를 통한 'Sent' 폴더 저장)
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


			// james DB 에서 Sent 폴더 ID 찾기 
			System.out.println(username);
			Integer mailboxId = jamesDao.getEmailboxSeq(username, "Sent");

			if (mailboxId != null) {

				// Oracle DB  필드 채우기 
				OracleEmailDTO Odto = new OracleEmailDTO();
				Odto.setEmailbox_seq(mailboxId.intValue());

				// James 서버의 Message-ID를  DTO 저장
				Odto.setJames_message_uid(message.getMessageID() != null ? message.getMessageID() : "NO_ID_" + System.currentTimeMillis());
				
				// JavaMail API로 데이터 추출한 후 DTO 저장
				Odto.setTitle(message.getSubject() != null ? message.getSubject() : "(제목 없음)");
  
				String fromAddress = InternetAddress.toString(message.getFrom()); 
				Odto.setEmail_from(fromAddress != null ? fromAddress : username);

				// Content 추출 (CLOB 저장용)
				Odto.setContent(extractTextFromMultipart(message.getContent())); 
	            
	            // Read 플래그 및 시간 설정
				Odto.setIs_read("y"); // 보낸 메일은 '읽음' 처리

	            // PUSH_AT (보낸 시간): SentDate 사용 (Timestamp 변환)
	            Date sentDate = message.getSentDate();
	            if (sentDate != null) {
	            	Odto.setPush_at(new Timestamp(sentDate.getTime()));
	            }
	            // PULL_AT (받은 시간): Sent 폴더이므로 NULL 또는 임시값 (NULL 유지)
	            Odto.setPull_at(null); 
	            

	            // c. EMAIL 테이블에 INSERT
	            oracleDao.insertEmail(Odto);
	            
	            

				// d. EMAIL_SENDER 테이블에 수신자 목록 INSERT
				int newEmailSeq = Odto.getEmail_seq();

				for (String recipient : toEmails) {
					//  dao.insertEmailSender의 인수는 emailSeq와 recipient
					oracleDao.insertEmailSender(newEmailSeq, recipient); 
				}

			} else {
				System.err.println("WARN: Sent 메일함 ID를 찾을 수 없어 Oracle DB 저장을 건너뜁니다.");
			}

		} catch (MessagingException e) {
			// James 서버 통신 오류 처리
			throw e; 
		} catch (Exception e) { 
			//  Oracle DB 접근 오류 (SQL Exception, DAO 오류 등)
			System.err.println("Oracle DB에 '보낸 메일' 저장 실패 (발송은 성공): " + e.getMessage());
			//  이 경우, 메일은 보내졌지만 React 목록에는 보이지 않는 불일치 상태가 됩니다.
		} finally {
			if (sentFolder != null && sentFolder.isOpen()) {
				sentFolder.close(false); // Close without expunge
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
	 * [수정] 메서드 이름을 getMailboxMessages로 변경하여 컨트롤러와 통일
	 * @param folderName 조회할 폴더 이름 (예: "INBOX", "Sent")
	 * @return Message[] 배열
	 */
	public List<JamesEmailDTO> getMailList(String email, String rawPassword, String folderName) throws Exception {
		Store store = null;
		Folder folder = null;
		List<JamesEmailDTO> mailList = new ArrayList<>();
		

		try {
	        
			//2. james 서버 연결 (java mail)
			Session imapsSession = getImapsSession();
			store = imapsSession.getStore("imaps");
			store.connect(mailHost, imapsPort, email, rawPassword);

			// folderName 인자에 따라 INBOX 또는 Sent 폴더를 엽니다.
			folder = store.getFolder(folderName);
			if (!folder.exists()) {
				return mailList; // 폴더가 없으면 빈 배열 반환
			}
			folder.open(Folder.READ_ONLY);
		
			

			Message[] messages = folder.getMessages();

			//  [수정] UIDFolder를 사용해 Message-ID를 얻기 위해 Header를 미리 로드
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.FLAGS);
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add("Message-ID"); // Message-ID 헤더를 미리 로드합니다.
			folder.fetch(messages, fp);

			// 2. 메시지 배열 정렬 (최신 메일을 가장 위로 - 내림차순)
			Arrays.sort(messages, new Comparator<Message>() {
				@Override
				public int compare(Message m1, Message m2) {
					try {
						Date d1 = ("Sent".equalsIgnoreCase(folderName)) ? m1.getSentDate() : m1.getReceivedDate();
						Date d2 = ("Sent".equalsIgnoreCase(folderName)) ? m2.getSentDate() : m2.getReceivedDate();

						if (d1 == null && d2 == null) return 0;
						if (d1 == null) return 1; 
						if (d2 == null) return -1;

						return d2.compareTo(d1); // 최신순 (내림차순)
					} catch (MessagingException e) {
						return 0;
					}
				}
			});




			//  Message 객체를 EmailDTO로 변환
			UIDFolder uf = (UIDFolder) folder;
			for (Message message : messages) {
				JamesEmailDTO dto = new JamesEmailDTO();

				// [핵심] IMAP UID 획득
				dto.setUid(uf.getUID(message)); 

				dto.setIs_read(message.getFlags().contains(Flags.Flag.SEEN) ? "y" : "n");

				// 날짜 설정 - 맞으면 수신 다르면 발신
				Date dateFromMessage = ("Sent".equalsIgnoreCase(folderName)) ? message.getSentDate() : message.getReceivedDate();
				
				if (dateFromMessage != null) {
			        dto.setReceived_date(new Timestamp(dateFromMessage.getTime())); // DTO 필드 유형 변경 가정
			    } else {
			    	dto.setReceived_date(null);
			    }
				
				
				
				// 나머지 필드 설정
				dto.setSender(InternetAddress.toString(message.getFrom()));
				dto.setSubject(message.getSubject() != null ? MimeUtility.decodeText(message.getSubject()) : "(제목 없음)");


				mailList.add(dto);
			}

			return mailList;

		} finally {
			// 연결 종료
			if (folder != null && folder.isOpen()) folder.close(false); 
			if (store != null && store.isConnected()) store.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

	//3. 메일 상세조회 메서드

	public JamesEmailDTO getMessageDetail(String email, String rawPassword, String folderName, long uid) throws Exception {
		Store store = null;
		Folder folder = null;

		try {
			Session imapsSession = getImapsSession();
			store = imapsSession.getStore("imaps");
			store.connect(mailHost, imapsPort, email, rawPassword);

			folder = store.getFolder(folderName);
			if (!folder.exists()) {
				throw new RuntimeException("메일 폴더를 찾을 수 없습니다: " + folderName);
			}
			folder.open(Folder.READ_WRITE); // 💡 읽음 처리를 위해 READ_WRITE

			UIDFolder uf = (UIDFolder) folder;
			Message message = uf.getMessageByUID(uid); 

			if (message == null) {
				throw new RuntimeException("메시지를 찾을 수 없습니다: UID " + uid);
			}



			// DTO 변환 (기본 정보)
			JamesEmailDTO dto = new JamesEmailDTO();
			dto.setUid(uid);
			dto.setSender(InternetAddress.toString(message.getFrom()));
			dto.setSubject(message.getSubject() != null ? MimeUtility.decodeText(message.getSubject()) : "(제목 없음)");

			dto.setIs_read(message.getFlags().contains(Flags.Flag.SEEN) ? "y" : "n");

			//  [핵심] 본문(Content) 파싱 및 설정
			dto.setContent(extractTextFromMultipart(message.getContent()));

			//  [수신자 목록] 설정
			dto.setMail_to(message.getRecipients(Message.RecipientType.TO));

			return dto;

		} finally {
			//  [최종 수정] close(true)로 닫아야 \Seen 플래그가 서버에 저장됨
			if (folder != null && folder.isOpen()) folder.close(true); 
			if (store != null && store.isConnected()) store.close();
		}
	}

	//  [신규 헬퍼] Message.getContent() 객체에서 텍스트 본문을 추출합니다.
	private String extractTextFromMultipart(Object content) throws Exception {
		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			for (int i = 0; i < multipart.getCount(); i++) {
				Part part = multipart.getBodyPart(i);

				if (part.isMimeType("text/html") || part.isMimeType("text/plain")) {
					if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) continue;
					return part.getContent().toString();
				}
			}
			return "(본문 내용 없음)"; 
		} else if (content instanceof String) {
			return (String) content;
		} else {
			return "메일 본문 형식 인식 불가";
		}
	}




	//  IMAPS 세션 획득 헬퍼
	private Session getImapsSession() {
		Properties props = new Properties();
		props.put("mail.store.protocol", "imaps");
		props.put("mail.imaps.host", mailHost);
		props.put("mail.imaps.port", imapsPort);
		//props.put("mail.imaps.ssl.enable", "true");
		// props.put("mail.imaps.ssl.trust", mailHost); // 로컬 인증서 무시
		props.put("mail.imaps.ssl.enable", "true");
		props.put("mail.imaps.ssl.checkserveridentity", "false"); //  [추가] 이름 검사 무시
		props.put("mail.imaps.ssl.trust", "*");                     // [추가] 모든 호스트 신뢰
		return Session.getDefaultInstance(props, null);
	}

	// ----------------------------------------------------
	// 4. 메일 삭제 기능 (IMAPS) - 기존 구현체
	// ----------------------------------------------------

	public void deleteAllEmails(String userEmail, String rawPassword) throws Exception {
		// ... (deleteAllEmails 로직은 getMailboxMessages 로직을 활용하여 구현 가능) ...
		// (Folder.READ_WRITE, message.setFlag(Flags.Flag.DELETED, true), inbox.expunge() 사용)
	}
}