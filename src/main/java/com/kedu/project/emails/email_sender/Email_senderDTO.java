package com.kedu.project.emails.email_sender;


/*
 	이메일 수신자 DTO
  */
public class Email_senderDTO {
	private int sender_seq; // 수신 고유번호
	private int email_seq; // 메일 고유번호
	private String sender_email; // 수신자 이메일( 사원 고유 아이디 )
	
	public Email_senderDTO() {}
	public Email_senderDTO(int sender_seq, int email_seq, String sender_email) {
		super();
		this.sender_seq = sender_seq;
		this.email_seq = email_seq;
		this.sender_email = sender_email;
	}
	public int getSender_seq() {
		return sender_seq;
	}
	public void setSender_seq(int sender_seq) {
		this.sender_seq = sender_seq;
	}
	public int getEmail_seq() {
		return email_seq;
	}
	public void setEmail_seq(int email_seq) {
		this.email_seq = email_seq;
	}
	public String getSender_email() {
		return sender_email;
	}
	public void setSender_email(String sender_email) {
		this.sender_email = sender_email;
	}
	
	
	
}
