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
	public int insertPirvateCahtRoom (Chat_roomDTO dto) {
		mybatis.insert("Chat_room.insertPirvateCahtRoom", dto);
		return dto.getChat_seq();
	}

	// 부서 단체 톡방 있는지 검사
	public int searchRoom(String departmentChatRoom) {
		return mybatis.selectOne("Chat_room.searchRoom",departmentChatRoom );
	}
	
	// 부서 단톡이 없을시 생성
	public int insetDepartmentRoom(Chat_roomDTO dto) {
		mybatis.insert("Chat_room.insetDepartmentRoom", dto);
		return dto.getChat_seq();
	}
	
	// 본인이 참여 되어있는 단체 채팅방 출력
	public List<Map<String, Object>> selectChatRoom(MemberDTO dto, String department){
		return mybatis.selectList("Chat_room.selectChatRoomList", Map.of(
				"email", dto.getEmail(),
				"department", department));
	}
	
	// 본인이 참여 되었던 종료된 채팅방 출력
	public List<Chat_roomDTO> completedList(MemberDTO dto){
		return mybatis.selectList("Chat_room.completedList", dto);
	}
	
	// 부서원 제외 개인 채팅방 정보 출력
	public Map<String, Object> chatRoom(Chat_memberDTO dto){
		return mybatis.selectOne("Chat_room.selectChatRoom", dto);
	}
	
	// 방 생성자(매니저) 이메일 출력
	public Chat_roomDTO selectChatRoomManager(Chat_roomDTO dto) {
		return mybatis.selectOne("Chat_room.selectChatRoomManager", dto);
	}
	
	// 채팅방 on/ off
	public int updateProjectOnOff(Chat_roomDTO dto) {
		return mybatis.update("Chat_room.updateProjectOnOff", dto);
	}



}
