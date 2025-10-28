package com.kedu.project.members.member_pto;

/*
 	보유 연차 DTO
 */

public class Member_ptoDTO {
	private String member_email; // 사원 고유 코드
	private int remaining_pto; // 남은 연차
	
	public Member_ptoDTO() {}
	public Member_ptoDTO(String member_email, int remaining_pto) {
		super();
		this.member_email = member_email;
		this.remaining_pto = remaining_pto;
	}
	
	
	public String getMember_email() {
		return member_email;
	}
	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}
	public int getRemaining_pto() {
		return remaining_pto;
	}
	public void setRemaining_pto(int remaining_pto) {
		this.remaining_pto = remaining_pto;
	}
	
	
}