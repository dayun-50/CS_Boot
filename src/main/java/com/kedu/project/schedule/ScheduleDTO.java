package com.kedu.project.schedule;

import java.sql.Timestamp;

/*
  	먼슬리 ( 일정 테이블 ) DTO
 */

public class ScheduleDTO {
	private int schedule_seq; // 일정 고유번호
	private String member_email; // 일정 생성자 아아디( 사원 고유 아이디 )
	private String title; // 일정 제목
	private String all_day; // 종일 여부 ( default : y, 아닐시 n )
	private String color; // 색상
	private Timestamp start_at; // 시작 날짜
	private Timestamp end_at; // 종료날짜
	private Timestamp created_at; // 일정 생성 날짜 ( default : sysdate )
	private int chat_seq; // 방정보
	
	public ScheduleDTO() {}

	public ScheduleDTO(int schedule_seq, String member_email, String title, String all_day, String color,
			Timestamp start_at, Timestamp end_at, Timestamp created_at, int chat_seq) {
		super();
		this.schedule_seq = schedule_seq;
		this.member_email = member_email;
		this.title = title;
		this.all_day = all_day;
		this.color = color;
		this.start_at = start_at;
		this.end_at = end_at;
		this.created_at = created_at;
		this.chat_seq = chat_seq;
	}

	public int getSchedule_seq() {
		return schedule_seq;
	}

	public void setSchedule_seq(int schedule_seq) {
		this.schedule_seq = schedule_seq;
	}

	public String getMember_email() {
		return member_email;
	}

	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAll_day() {
		return all_day;
	}

	public void setAll_day(String all_day) {
		this.all_day = all_day;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Timestamp getStart_at() {
		return start_at;
	}

	public void setStart_at(Timestamp start_at) {
		this.start_at = start_at;
	}

	public Timestamp getEnd_at() {
		return end_at;
	}

	public void setEnd_at(Timestamp end_at) {
		this.end_at = end_at;
	}

	public Timestamp getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}

	public int getChat_seq() {
		return chat_seq;
	}

	public void setChat_seq(int chat_seq) {
		this.chat_seq = chat_seq;
	}
	
}
