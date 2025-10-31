package com.kedu.project.file;

import java.sql.Timestamp;

/*
  	파일 업로드 및 다운로드 DTO
  */

public class FileDTO {
	private int file_seq; // 파일 고유번호
	private String member_email; // 사원 고유 아이디 (올린이)
	private String sysname; // 시스템 저장 파일명
	private String oriname; // 원본 파일명
	private Timestamp upload_at; // 업로드 날짜 ( default : sysdate )
	private String file_type; // 파일 종류 코드
	private int parent_seq; // 부모 시퀀스
	private Integer chat_seq; // 채팅메세지시퀀스
	
	
	
	public FileDTO(int file_seq, String member_email, String sysname, String oriname, Timestamp upload_at,
			String file_type, int parent_seq, Integer chat_seq) {
		super();
		this.file_seq = file_seq;
		this.member_email = member_email;
		this.sysname = sysname;
		this.oriname = oriname;
		this.upload_at = upload_at;
		this.file_type = file_type;
		this.parent_seq = parent_seq;
		this.chat_seq = chat_seq;
	}

	public FileDTO() {}

	public int getFile_seq() {
		return file_seq;
	}

	public void setFile_seq(int file_seq) {
		this.file_seq = file_seq;
	}

	public String getMember_email() {
		return member_email;
	}

	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}

	public String getSysname() {
		return sysname;
	}

	public void setSysname(String sysname) {
		this.sysname = sysname;
	}

	public String getOriname() {
		return oriname;
	}

	public void setOriname(String oriname) {
		this.oriname = oriname;
	}

	public Timestamp getUpload_at() {
		return upload_at;
	}

	public void setUpload_at(Timestamp upload_at) {
		this.upload_at = upload_at;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public int getParent_seq() {
		return parent_seq;
	}

	public void setParent_seq(int parent_seq) {
		this.parent_seq = parent_seq;
	}

	public Integer getChat_seq() {
		return chat_seq;
	}

	public void setChat_seq(Integer chat_seq) {
		this.chat_seq = chat_seq;
	}

	
}
