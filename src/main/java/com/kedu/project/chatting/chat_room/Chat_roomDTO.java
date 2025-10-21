package com.kedu.project.chatting.chat_room;


/*
  	채팅방 정보 DTO
  */
public class Chat_roomDTO {
	private int chat_seq; // 채팅방 고유번호
	private String chat_name; // 채팅방 이름
	private String manager_email; // 채팅방 권한자 아이디( 생성자 )
	private String project_progress; // 프로젝트 진행현황 ( default : y, 종료 : n )
	
	public Chat_roomDTO() {}
	public Chat_roomDTO(int chat_seq, String chat_name, String manager_email, String project_progress) {
		super();
		this.chat_seq = chat_seq;
		this.chat_name = chat_name;
		this.manager_email = manager_email;
		this.project_progress = project_progress;
	}
	public int getChat_seq() {
		return chat_seq;
	}
	public void setChat_seq(int chat_seq) {
		this.chat_seq = chat_seq;
	}
	public String getChat_name() {
		return chat_name;
	}
	public void setChat_name(String chat_name) {
		this.chat_name = chat_name;
	}
	public String getManager_email() {
		return manager_email;
	}
	public void setManager_email(String manager_email) {
		this.manager_email = manager_email;
	}
	public String getProject_progress() {
		return project_progress;
	}
	public void setProject_progress(String project_progress) {
		this.project_progress = project_progress;
	}
	
	
}
