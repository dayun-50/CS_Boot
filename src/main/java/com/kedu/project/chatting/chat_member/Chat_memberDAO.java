package com.kedu.project.chatting.chat_member;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kedu.project.chatting.chat_room.Chat_roomDTO;
import com.kedu.project.members.member.MemberDTO;

/*
	채팅방 참여자 관리 DAO
 */
@Repository
public class Chat_memberDAO {
	@Autowired
	private SqlSession mybatis;

//	// 채팅방 존재 여부 및 존재시 채팅방 seq 반환
//	public int checkPrivateChat(MemberDTO dto, String timeEmail) {
//		return mybatis.selectOne("Chat_member.checkPrivateChat", Map.of(
//				"email1", dto.getEmail(),
//				"email2", timeEmail));
//	}
	
	// 채팅방 존재 여부 및 존재시 채팅방 seq 반환
		public int checkPrivateChat(MemberDTO dto, String timeEmail, String depChatName) {
			return mybatis.selectOne("Chat_member.checkPrivateChat", Map.of(
					"email1", dto.getEmail(),
					"email2", timeEmail, 
					"depChatName", depChatName));
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

	// last_message_seq insert
	public int updateLastMessageSeq(Chat_memberDTO dto) {
		return mybatis.update("Chat_member.updateLastMessageSeq", dto);
	}

	// 채팅방 멤버 뽑기
	public List<String> getMembersByRoomSeq(int chat_seq){
		return mybatis.selectList("Chat_member.selectMember", chat_seq);
	}

	// 마지막으로 읽은 메세지 시퀀스 뽑기
	public int getLastMessageSeq(String email, int chatSeq) {
		return mybatis.selectOne("Chat_member.getLastMessageSeq", Map.of(
				"member_email", email,
				"chat_seq", chatSeq));
	}

	// 채팅방 인원 측정
	public int memberCount(String chat_seq) {
		return mybatis.selectOne("Chat_member.countMember", chat_seq);
	}

	// 채팅방 나가기
	public int outChat(Chat_memberDTO dto) {
		return mybatis.delete("Chat_member.outChat", dto);
	}

}
