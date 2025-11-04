package com.kedu.project.emails.email;

import java.sql.Timestamp;

public class OracleEmailDTO {
	
	// 1. Oracle PK/FK 및 James 연결 키
    private int email_seq;               // EMAIL_SEQ (PK, Oracle 시퀀스로 채워짐)
    private Integer emailbox_seq;        // EMAILBOX_SEQ (James MAILBOX_ID FK)
    private String james_message_uid;     // JAMES_MESSAGE_UID
    
    // 2. 메타데이터 (DB에 저장되는 필드)
    private String title;               // TITLE
    private String email_from;           // EMAIL_FROM
    private String content;             // CONTENT (CLOB)
    private String is_read;                // READ ('y'/'n')
    private Timestamp push_at;           // PUSH_AT (보낸 시간)
    private Timestamp pull_at;           // PULL_AT (받은 시간)
    
    
    
    
	public OracleEmailDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public OracleEmailDTO(int email_seq, Integer emailbox_seq, String james_message_uid, String title,
			String email_from, String content, String is_read, Timestamp push_at, Timestamp pull_at) {
		super();
		this.email_seq = email_seq;
		this.emailbox_seq = emailbox_seq;
		this.james_message_uid = james_message_uid;
		this.title = title;
		this.email_from = email_from;
		this.content = content;
		this.is_read = is_read;
		this.push_at = push_at;
		this.pull_at = pull_at;
	}
	public int getEmail_seq() {
		return email_seq;
	}
	public void setEmail_seq(int email_seq) {
		this.email_seq = email_seq;
	}
	public Integer getEmailbox_seq() {
		return emailbox_seq;
	}
	public void setEmailbox_seq(Integer emailbox_seq) {
		this.emailbox_seq = emailbox_seq;
	}
	public String getJames_message_uid() {
		return james_message_uid;
	}
	public void setJames_message_uid(String james_message_uid) {
		this.james_message_uid = james_message_uid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEmail_from() {
		return email_from;
	}
	public void setEmail_from(String email_from) {
		this.email_from = email_from;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getIs_read() {
		return is_read;
	}
	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}
	public Timestamp getPush_at() {
		return push_at;
	}
	public void setPush_at(Timestamp push_at) {
		this.push_at = push_at;
	}
	public Timestamp getPull_at() {
		return pull_at;
	}
	public void setPull_at(Timestamp pull_at) {
		this.pull_at = pull_at;
	}
    
    
    
    
    
    
    
    
    
    
    
    
	
    
    
    

}
