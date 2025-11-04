package com.kedu.project.emails.email;

public class EmailSendRequestDTO {
	private String subject;         // 메일 제목
    private String content;         // 메일 본문 내용
    private String receiverEmails;  // 쉼표(,)로 구분된 수신자 전체 목록 (API 통신용)
    
    
    
    
    
	public EmailSendRequestDTO() {
		super();
	
	}
	public EmailSendRequestDTO(String subject, String content, String receiverEmails) {
		super();
		this.subject = subject;
		this.content = content;
		this.receiverEmails = receiverEmails;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReceiverEmails() {
		return receiverEmails;
	}
	public void setReceiver_emails(String receiverEmails) {
		this.receiverEmails = receiverEmails;
	}
	
    
    
    
    
    
    
    
    
}
