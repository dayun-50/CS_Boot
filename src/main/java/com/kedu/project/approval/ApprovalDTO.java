package com.kedu.project.approval;


/*
 	전자결제 DTO
  */
public class ApprovalDTO {
	private int approval_seq; // 연자 고유번호
	private String member_email; // 사원 고유 아이디
	private String approval_title; // 결재 제목
	private String approval_content; // 결재 내용
	private String approval_at; // 요청 일시 ( default: sysdate ) 
	private String approval_status; // 승인 상태 ( default: w(대기), 반려: n, 승인: y )
	
	public ApprovalDTO() {}
	public ApprovalDTO(int approval_seq, String member_email, String approval_title, String approval_content,
			String approval_at, String approval_status) {
		super();
		this.approval_seq = approval_seq;
		this.member_email = member_email;
		this.approval_title = approval_title;
		this.approval_content = approval_content;
		this.approval_at = approval_at;
		this.approval_status = approval_status;
	}


	public int getApproval_seq() {
		return approval_seq;
	}

	public void setApproval_seq(int approval_seq) {
		this.approval_seq = approval_seq;
	}

	public String getMember_email() {
		return member_email;
	}

	public void setMember_email(String member_email) {
		this.member_email = member_email;
	}

	public String getApproval_title() {
		return approval_title;
	}

	public void setApproval_title(String approval_title) {
		this.approval_title = approval_title;
	}

	public String getApproval_content() {
		return approval_content;
	}

	public void setApproval_content(String approval_content) {
		this.approval_content = approval_content;
	}

	public String getApproval_at() {
		return approval_at;
	}

	public void setApproval_at(String approval_at) {
		this.approval_at = approval_at;
	}

	public String getApproval_status() {
		return approval_status;
	}

	public void setApproval_status(String approval_status) {
		this.approval_status = approval_status;
	}
	
	
	
}
