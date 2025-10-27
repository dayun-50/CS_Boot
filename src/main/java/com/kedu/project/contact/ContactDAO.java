package com.kedu.project.contact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
/*
 *  연락처 기능 구현 DAO
 */

@Repository
public class ContactDAO {
	@Autowired
	private SqlSession mybatis;

	public List<ContactDTO> getMyContacts() {
		return mybatis.selectList("Contact.getMyContacts");
	}

	public int insertContact(ContactDTO dto) {
		return mybatis.insert("Contact.insertContact", dto);
	}

	public int deleteContact(int contact_seq) {
		return mybatis.delete("Contact.deleteContact", contact_seq);
	}

	// 분류 버튼 적용시 - 업데이트일 시에도 적용
	public int updateContact(ContactDTO dto) {
	    return mybatis.update("Contact.updateContact", dto);
	}

}
