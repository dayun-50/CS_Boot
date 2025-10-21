package com.kedu.project.chatting.chat_room;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kedu.project.chatting.chat_member.Chat_memberDTO;
import com.kedu.project.members.member.MemberDTO;

/*
  	채팅방 생성 및 프로젝트 on off 기능 구현 DAO
 */
@Repository
public class Chat_roomDAO {
	@Autowired
	private SqlSession mybatis;

	// 개인 채팅방 생성 후 seq 뽑아서 보내기
	public int insertPirvateCahtRoom (Chat_memberDTO dto) {
		mybatis.insert("Chat_room.insertPirvateCahtRoom", dto);
		return dto.getChat_seq();
	}

	// 부서 단체 톡방 있는지 검사
	public int searchRoom(MemberDTO dto, String departmentChatRoom) {
		return mybatis.selectOne("Chat_room.searchRoom", Map.of(
				"manager_email", dto.getEmail(),
				"departmentChatRoom", departmentChatRoom));
	}
	
	// 부서 단톡이 없을시 생성
	public int insetDepartmentRoom(Chat_roomDTO dto) {
		mybatis.insert("Chat_room.insetDepartmentRoom", dto);
		return dto.getChat_seq();
	}
	
	// 본인이 참여 되어있는 채팅방 출력
	public List<Map<String, Object>> selectChatRoom(MemberDTO dto, String department){
		return mybatis.selectList("Chat_room.selectChatRoom", Map.of(
				"email", dto.getEmail(),
				"department", department));
	}

}
