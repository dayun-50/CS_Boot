package com.kedu.project.contact;

/*
  	연락처 DTO
 */
public class ContactDTO {
	private int contact_seq; // 연락처 고유번호
	private String owner_email; // 연락처 주인 고유 아이디(사원 고유 아이디)
	private String name; // 추가 연락처 이름
	private String email; // 추가 연락처 이메일
	private String phone; // 추가 연락처 전화번호
	private String contact_group; // 연락처 회사명
	private String company_code; // 회사 고유 코드
	private String memo; // 메모 내용
	private String share; // 팀 공유 여부 (default: n, 공유 설정 시: y)

	public ContactDTO() {
	}

	public ContactDTO(int contact_seq, String owner_email, String name, String email, String phone,
			String contact_group, String company_code, String memo, String share) {
		super();
		this.contact_seq = contact_seq;
		this.owner_email = owner_email;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.contact_group = contact_group;
		this.company_code = company_code;
		this.memo = memo;
		this.share = share;
	}

	public int getContact_seq() {
		return contact_seq;
	}

	public void setContact_seq(int contact_seq) {
		this.contact_seq = contact_seq;
	}

	public String getOwner_email() {
		return owner_email;
	}

	public void setOwner_email(String owner_email) {
		this.owner_email = owner_email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContact_group() {
		return contact_group;
	}

	public void setContact_group(String contact_group) {
		this.contact_group = contact_group;
	}

	public String getCompany_code() {
		return company_code;
	}

	public void setCompany_code(String company_code) {
		this.company_code = company_code;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

}
