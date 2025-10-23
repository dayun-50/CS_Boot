package com.kedu.project.chatting.chat_member;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kedu.project.members.member.MemberDTO;

/*
	채팅방 참여자 관리 DAO
 */
@Repository
public class Chat_memberDAO {
	@Autowired
	private SqlSession mybatis;

	// 채팅방 존재 여부 및 존재시 채팅방 seq 반환
	public int checkPrivateChat(MemberDTO dto, String timeEmail) {
		return mybatis.selectOne("Chat_member.checkPrivateChat", Map.of(
				"email1", dto.getEmail(),
				"email2", timeEmail));
	}

	// 채팅방 존재 여부에 없을시 생성 후 멤버 insert
	public int insertCahtMember(Chat_memberDTO dto) {
		return mybatis.insert("Chat_member.insertChat", dto);
	}

	// 부서 단톡에 내가 있는지 없는지
	public int existDepartmentRoom(MemberDTO dto, int seq) {
		return mybatis.selectOne("Chat_member.existDepartmentRoom", Map.of(
				"email", dto.getEmail(),
				"chat_seq", seq));
	}
	
}
