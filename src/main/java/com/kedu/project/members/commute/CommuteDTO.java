package com.kedu.project.members.commute;

import java.sql.Timestamp;
import java.time.LocalDate;

/*
 	근태 관리용 DTO
 
 */

public class CommuteDTO {
	private String member_email; // 사원 고유 아이디
	private String lateness; // 지각여부 ( default: n , y )
	private String leave_early; // 조퇴여부 ( default: n , y )
	private String absence; // 결근여부 ( default : 출근, 연차, 결근 )
	private Timestamp work_at; // 출근시간
	private Timestamp leave_at; // 퇴근시간
	private LocalDate  commute_at; // 오늘 날짜 ( default : sysdate )
	
	public CommuteDTO () {}
	public CommuteDTO(String member_email, String lateness, String leave_early, String absence, Timestamp work_at,
			Timestamp leave_at, LocalDate commute_at) {
		super();
		this.member_email = member_email;
		this.lateness = lateness;
		this.leave_early = leave_early;
		this.absence = absence;
		this.work_at = work_at;
		this.leave_at = leave_at;
		this.commute_at = commute_at;
	}
	public String getMember_email() {
		return member_email;
	}
	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}
	public String getLateness() {
		return lateness;
	}
	public void setLateness(String lateness) {
		this.lateness = lateness;
	}
	public String getLeave_early() {
		return leave_early;
	}
	public void setLeave_early(String leave_early) {
		this.leave_early = leave_early;
	}
	public String getAbsence() {
		return absence;
	}
	public void setAbsence(String absence) {
		this.absence = absence;
	}
	public Timestamp getWork_at() {
		return work_at;
	}
	public void setWork_at(Timestamp work_at) {
		this.work_at = work_at;
	}
	public Timestamp getLeave_at() {
		return leave_at;
	}
	public void setLeave_at(Timestamp leave_at) {
		this.leave_at = leave_at;
	}
	public LocalDate getCommute_at() {
		return commute_at;
	}
	public void setCommute_at(LocalDate commute_at) {
		this.commute_at = commute_at;
	}
	
	
}
