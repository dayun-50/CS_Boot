package com.kedu.project.emails.email;

import java.util.List;

import jakarta.mail.Address;

/*
  	이메일 DTO
  */
public class EmailDTO {
	// 목록 표시  (모든 API에서 반환)
    private long uid;               // IMAP 메시지 UID (상세 조회의 키)
    private String sender;          // 보낸 사람
    private String subject;         // 제목
    private String receivedDate;      // 받은 날짜 (정렬 기준)
    private String isRead;          // 읽음 여부 ('y' / 'n')
    
    // 메일 상세 보기  (상세 조회 API에서만 값이 채워짐)
    private String content;          // 메일 본문 내용 (HTML 또는 Plain Text)
    private List<String> attachments; // 첨부파일 목록 (파일 이름만)
    private Address[] mailTo;        // Message.getRecipients() 결과 (실제 수신자 목록)
    
 //  DB 영속성 및 연결 키
    private int emailbox_seq;       // Oracle DB의 EMAILBOX 테이블 FK
    private int email_seq;          // Oracle DB의 EMAIL 테이블 PK (자동 생성 후 반환용)
    private String james_message_uid; // Oracle DB에 저장할 James 메시지의 Message-ID
    
    
    
    
    
    
    
    
    
    
    
    
	


	public EmailDTO() {
	}
	
	
	public EmailDTO(int emailbox_seq, String james_message_uid, String subject, String content, String sender, String isRead) {
        this.emailbox_seq = emailbox_seq;
        this.james_message_uid = james_message_uid;
        this.subject = subject;
        this.content = content;
        this.sender = sender;
        this.isRead = isRead;
        // email_seq는 DB에서 자동 생성되므로 받지 않습니다.
    }
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	public Address[] getMailTo() {
		return mailTo;
	}
	public void setMailTo(Address[] mailTo) {
		this.mailTo = mailTo;
	}
	
	public int getEmailbox_seq() {
		return emailbox_seq;
	}


	public void setEmailbox_seq(int emailbox_seq) {
		this.emailbox_seq = emailbox_seq;
	}


	public int getEmail_seq() {
		return email_seq;
	}


	public void setEmail_seq(int email_seq) {
		this.email_seq = email_seq;
	}


	public String getJames_message_uid() {
		return james_message_uid;
	}


	public void setJames_message_uid(String james_message_uid) {
		this.james_message_uid = james_message_uid;
	}
	
	
    
    
    
    
	
}
