package com.kedu.project.contact;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
/*
 *  연락처 기능 구현 DAO
 */

import com.kedu.project.chatting.chat_member.Chat_memberDTO;

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
	
	// 채널 추가 주소록 출력 
	public List<ContactDTO> contactList(String eamil){
		return mybatis.selectList("Contact.contactList", eamil);
	}
	
	// 연락처에 email 출력
	public String selectName(int seq) {
		return mybatis.selectOne("Contact.selectName", seq);
	}

}
