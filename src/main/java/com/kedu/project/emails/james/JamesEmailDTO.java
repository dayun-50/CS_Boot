package com.kedu.project.emails.james;

import java.sql.Timestamp;
import java.util.List;

import jakarta.mail.Address;

/*
  	이메일 DTO
  */
public class JamesEmailDTO {
	// 목록 표시  (모든 API에서 반환)
    private long uid;               // IMAP 메시지 UID (상세 조회의 키)
    private String sender;          // 보낸 사람
    private String subject;         // 제목
    private Timestamp received_date;      // 받은 날짜 (정렬 기준)
    private String is_read;          // 읽음 여부 ('y' / 'n')
    
    // 메일 상세 보기  (상세 조회 API에서만 값이 채워짐)
    private String content;          // 메일 본문 내용 (HTML 또는 Plain Text)
    private List<String> attachments; // 첨부파일 목록 (파일 이름만)
    private Address[] mail_to;        // Message.getRecipients() 결과 (실제 수신자 목록)
    
    
    
    
    
    
	public JamesEmailDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public JamesEmailDTO(long uid, String sender, String subject, Timestamp received_date, String is_read,
			String content, List<String> attachments, Address[] mail_to) {
		super();
		this.uid = uid;
		this.sender = sender;
		this.subject = subject;
		this.received_date = received_date;
		this.is_read = is_read;
		this.content = content;
		this.attachments = attachments;
		this.mail_to = mail_to;
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
	public Timestamp getReceived_date() {
		return received_date;
	}
	public void setReceived_date(Timestamp received_date) {
		this.received_date = received_date;
	}
	public String getIs_read() {
		return is_read;
	}
	public void setIs_read(String is_read) {
		this.is_read = is_read;
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
	public Address[] getMail_to() {
		return mail_to;
	}
	public void setMail_to(Address[] mail_to) {
		this.mail_to = mail_to;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
    
    
	
}
