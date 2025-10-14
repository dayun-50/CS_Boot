package com.kedu.project.chatting.chat_message;

import java.security.Timestamp;

/*
  채팅 메세지 DTO
  */
public class Chat_messageDTO {
	private int message_seq; // 메세지 고유번호
	private int chat_seq; // 채팅방 고유번호( 부모 시퀀스 )
	private String member_email; // 사원 고유 아이디
	private String message; // 메세지 내용
	private Timestamp message_at; // 보낸 시간 ( default: sysdate )
	
	public Chat_messageDTO() {}
	public Chat_messageDTO(int message_seq, int chat_seq, String member_email, String message, Timestamp message_at) {
		super();
		this.message_seq = message_seq;
		this.chat_seq = chat_seq;
		this.member_email = member_email;
		this.message = message;
		this.message_at = message_at;
	}
	public int getMessage_seq() {
		return message_seq;
	}
	public void setMessage_seq(int message_seq) {
		this.message_seq = message_seq;
	}
	public int getChat_seq() {
		return chat_seq;
	}
	public void setChat_seq(int chat_seq) {
		this.chat_seq = chat_seq;
	}
	public String getMember_email() {
		return member_email;
	}
	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Timestamp getMessage_at() {
		return message_at;
	}
	public void setMessage_at(Timestamp message_at) {
		this.message_at = message_at;
	}
	
	
}
