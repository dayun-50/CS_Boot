package com.kedu.project.contact;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.members.member.MemberDAO;

@Service
public class ContactService {
	@Autowired
	private ContactDAO dao;
	
	@Autowired
    private MemberDAO memberDao;


	// 리스트
	public List<ContactDTO> getMyContacts() {
		return dao.getMyContacts();
	}

	public int insertContact(ContactDTO dto) {
	    String email = dto.getOwner_email(); // 프론트에서 들어온 이메일
	    String companyCode = memberDao.getCompanyCodeByEmail(email); // 여기가 null이면 문제

	    System.out.println("owner_email: " + email);
	    System.out.println("company_code 조회 결과: " + companyCode); // 디버깅용

	    dto.setCompany_code(companyCode); // null이면 에러 발생 가능

	    return dao.insertContact(dto);
	}

	public int updateContact(ContactDTO dto) {
		return dao.updateContact(dto);
	}

	public int deleteContact(int contact_seq) {
		return dao.deleteContact(contact_seq);
	}

	// 분류 버튼 적용시
	public boolean updateContactGroup(int contact_seq, String share) {
		// 수정 시 : DAO가 반환한 int(영향받은 행 수)를 boolean으로 변환
		int rowsAffected = dao.updateContactGroup(contact_seq, share);
		return rowsAffected > 0; // 1개 이상의 행이 업데이트되었으면 true 반환
	}
}
