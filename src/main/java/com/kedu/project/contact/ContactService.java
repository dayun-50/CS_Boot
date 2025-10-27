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

	// 연락처 목록 조회
	public List<ContactDTO> getMyContacts() {
		return dao.getMyContacts();
	}

	// 연락처 등록
	public int insertContact(ContactDTO dto) {
		return dao.insertContact(dto);
	}

	// 연락처 수정 (share 포함) - 분류 버튼 클릭시에도 적용
	public boolean updateContact(ContactDTO dto) {
		int rows = dao.updateContact(dto);
		return rows > 0;
	}

	// 연락처 삭제
	public int deleteContact(int contact_seq) {
		return dao.deleteContact(contact_seq);
	}

}
