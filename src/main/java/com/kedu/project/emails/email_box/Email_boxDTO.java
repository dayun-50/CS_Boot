package com.kedu.project.emails.email_box;


/*
  	메일함 DTO
  */
public class Email_boxDTO {
	private int emailbox_seq; // 메일함 고유번호
	private String member_email; // 사원 고유 아이디
	private String emailbox_type; // 메일함 종류
	
	public Email_boxDTO() {}
	public Email_boxDTO(int emailbox_seq, String member_email, String emailbox_type) {
		super();
		this.emailbox_seq = emailbox_seq;
		this.member_email = member_email;
		this.emailbox_type = emailbox_type;
	}
	public int getEmailbox_seq() {
		return emailbox_seq;
	}
	public void setEmailbox_seq(int emailbox_seq) {
		this.emailbox_seq = emailbox_seq;
	}
	public String getMember_email() {
		return member_email;
	}
	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}
	public String getEmailbox_type() {
		return emailbox_type;
	}
	public void setEmailbox_type(String emailbox_type) {
		this.emailbox_type = emailbox_type;
	}
	
	
}
