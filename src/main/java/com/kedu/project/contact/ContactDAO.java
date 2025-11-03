package com.kedu.project.contact;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ContactDAO {
	@Autowired
	private SqlSession mybatis;

	// 본인 연락처 목록 조회
	public List<ContactDTO> contactList(String ownerEmail) {
		return mybatis.selectList("Contact.contactList", ownerEmail);
	}

	// 연락처 등록
	public int insertContact(ContactDTO dto) {
		return mybatis.insert("Contact.insertContact", dto);
	}

	// 연락처 수정 (본인 소유 확인)
	public int updateContact(ContactDTO dto) {
		// WHERE CONTACT_SEQ = #{contact_seq} AND OWNER_EMAIL = #{owner_email}
		return mybatis.update("Contact.updateContact", dto);
	}

	// 연락처 삭제 (본인 소유 확인)
	public int deleteContactByOwner(int contact_seq, String ownerEmail) {
		return mybatis.delete("Contact.deleteContactByOwner", new java.util.HashMap<String, Object>() {
			{
				put("contact_seq", contact_seq);
				put("owner_email", ownerEmail);
			}
		});
	}

	// 이메일 조회
	public String selectName(int seq) {
		return mybatis.selectOne("Contact.selectName", seq);
	}

	// 개인용 - n
	public List<ContactDTO> selectIndividualContacts(String owner_email) {
		return mybatis.selectList("Contact.selectIndividualContacts", owner_email);
	}

	// 팀원용 - y
	public List<ContactDTO> selectTeamContact(Map<String, Object> params) { // <- 메서드 이름 단수형으로 통일
		// 💡 XML에 정의된 ID: selectTeamContact (단수)
		return mybatis.selectList("Contact.selectTeamContact", params);
	}

}
