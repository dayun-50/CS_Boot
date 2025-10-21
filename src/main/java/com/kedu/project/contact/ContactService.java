package com.kedu.project.contact;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
	@Autowired
	private ContactDAO dao;

	// 리스트
	public List<ContactDTO> getMyContacts() {
		return dao.getMyContacts();
	}

	public int insertContact(ContactDTO dto) {
		return dao.insertContact(dto);
	}

	public int updateContact(ContactDTO dto) {
		return dao.updateContact(dto);
	}

	public int deleteContact(int contact_seq) {
		return dao.deleteContact(contact_seq);
	}
}
