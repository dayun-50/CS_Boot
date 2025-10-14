package com.kedu.project.chatting.chat_member;

/*
   채팅방 참여자 정보 DTO
  */

public class Chat_memberDTO {
	private int chat_seq; // 채팅방 고유번호
	private String member_email; // 참여자 고유 아이디(사원)
	private String role; // 권한( 방장: m(매니저), 일반: n )
	
	public Chat_memberDTO() {}
	public Chat_memberDTO(int chat_seq, String member_email, String role) {
		super();
		this.chat_seq = chat_seq;
		this.member_email = member_email;
		this.role = role;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
}
