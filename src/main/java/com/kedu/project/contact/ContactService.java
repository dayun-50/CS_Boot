package com.kedu.project.contact;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.members.member.MemberDAO;
import com.kedu.project.members.member.MemberDTO;

@Service
public class ContactService {
	@Autowired
	private ContactDAO dao;
	
	@Autowired
	private MemberDAO memberDAO;

	// 본인 연락처 목록 조회
	public List<ContactDTO> getContactsByOwner(String ownerEmail) {
		return dao.contactList(ownerEmail);
	}

	// 연락처 등록
	public int insertContact(ContactDTO dto) {
		if (dto.getCompany_code() == null) {
			dto.setCompany_code("");
		}
		
		// MEMO도 NOT NULL 제약 조건일 수 있으므로 함께 확인
		if (dto.getMemo() == null) {
			dto.setMemo("");
		}

		return dao.insertContact(dto);
	}

	// 연락처 수정 (본인 소유만)
	public boolean updateContact(ContactDTO dto) {
		int rows = dao.updateContact(dto);
		return rows > 0;
	}

	// 연락처 삭제 (본인 소유만)
	public int deleteContactByOwner(int contact_seq, String ownerEmail) {
		return dao.deleteContactByOwner(contact_seq, ownerEmail);
	}

	// 개인용 - n
	public List<ContactDTO> selectIndividualContacts(String owner_email) {
		return dao.selectIndividualContacts(owner_email);
	}

	// 팀원용 - y
	public List<ContactDTO> selectTeamContact(Map<String, Object> params) {
		return dao.selectTeamContact(params); // <- dao.selectTeamContact (단수형) 호출
	}

	// -------------------- 주소록에 좀 뽑을게 --------------------------------
	// 이메일로 company_code 조회 - 주소록 추가시 팔요하여 넣음
	public String getCompanyCodeByEmail(String email) {
		MemberDTO member = memberDAO.findByEmail(email);
		return member != null ? member.getCompany_code() : null;
	}

	// 부서
	public String getDeptCodeByEmail(String email) {
		// DAO를 통해 실제 부서 코드(DEPT_CODE)를 조회하도록 수정
		return memberDAO.getDeptCodeByEmail(email);
	}	
}
