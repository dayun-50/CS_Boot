package com.kedu.project.emails.email;

import java.security.Timestamp;

/*
  	이메일 DTO
  */
public class EmailDTO {
	private int email_seq; // 메일 고유번호
	private int emailbox_seq; // 메일함 고유번호
	private String title; // 메일 제목
	private String meail_from; // 보낸사람 이메일
	private String content; // 메일 내용
	private Timestamp push_at; // 발송 날짜 ( default : sysdate )
	private Timestamp pull_at; // 수신 날짜
	private String read; // 읽음 여부( 확인: y, 미확인: n)
	
	public EmailDTO() {}
	public EmailDTO(int email_seq, int emailbox_seq, String title, String meail_from, String content, Timestamp push_at,
			Timestamp pull_at, String read) {
		super();
		this.email_seq = email_seq;
		this.emailbox_seq = emailbox_seq;
		this.title = title;
		this.meail_from = meail_from;
		this.content = content;
		this.push_at = push_at;
		this.pull_at = pull_at;
		this.read = read;
	}
	public int getEmail_seq() {
		return email_seq;
	}
	public void setEmail_seq(int email_seq) {
		this.email_seq = email_seq;
	}
	public int getEmailbox_seq() {
		return emailbox_seq;
	}
	public void setEmailbox_seq(int emailbox_seq) {
		this.emailbox_seq = emailbox_seq;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMeail_from() {
		return meail_from;
	}
	public void setMeail_from(String meail_from) {
		this.meail_from = meail_from;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	
	
}
