package com.kedu.project.members.member_pto;

import java.sql.Timestamp;

/*
 	보유 연차 DTO
 */

public class Member_ptoDTO {
	private String member_email; // 사원 고유 코드
	private double remaining_pto; // 남은 연차 -- 1일,2일로 계산
	private Timestamp last_granted_at;
	
	public Member_ptoDTO() {}
	public Member_ptoDTO(String member_email, double remaining_pto, Timestamp last_granted_at) {
		super();
		this.member_email = member_email;
		this.remaining_pto = remaining_pto;
		this.last_granted_at = last_granted_at;
	}
	public String getMember_email() {
		return member_email;
	}
	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}
	public double getRemaining_pto() {
		return remaining_pto;
	}
	public void setRemaining_pto(double remaining_pto) {
		this.remaining_pto = remaining_pto;
	}
	public Timestamp getLast_granted_at() {
		return last_granted_at;
	}
	public void setLast_granted_at(Timestamp last_granted_at) {
		this.last_granted_at = last_granted_at;
	}
	
	
	
	
}