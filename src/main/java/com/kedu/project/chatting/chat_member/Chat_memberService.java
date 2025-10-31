package com.kedu.project.chatting.chat_member;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.chatting.chat_message.Chat_messageDAO;
import com.kedu.project.chatting.chat_room.Chat_roomDAO;
import com.kedu.project.chatting.chat_room.Chat_roomDTO;
import com.kedu.project.contact.ContactDAO;
import com.kedu.project.contact.ContactDTO;
import com.kedu.project.members.member.MemberDAO;
import com.kedu.project.members.member.MemberDTO;


/*
	채팅방 참여자 관리 Service
 */
@Service
public class Chat_memberService {
	@Autowired
	private Chat_memberDAO dao;

	@Autowired
	private Chat_roomDAO roomDao;

	@Autowired
	private Chat_messageDAO cMDao;

	@Autowired
	private MemberDAO memberDao;

	@Autowired
	private ContactDAO ContactDAO;

	// 팀원간 개인 메세지 목록 출력 및 생성(없을시)
	public List<Map<String, Object>> privatChatSearch(MemberDTO dto){
		// 같은 부서의 팀 정보 출력
		List<MemberDTO> memberList = memberDao.memberSearch(dto);
		String department = memberDao.selectDepartment(dto);
		String depChatName = (department+" 단체 채팅");
		System.out.println("멤버서칭"+memberList);
		List<Map<String, Object>> list = new ArrayList<>();
		for(MemberDTO members : memberList) {
			// 채팅방 존재 여부 및 존재시 채팅방 seq 반환
			int checkChat = dao.checkPrivateChat(dto, members.getEmail(),depChatName);
			System.out.println("개인채팅ㅌ"+checkChat);
			Map<String, Object> map = new HashMap<>();
			if(checkChat > 0) { // 채팅방 존재시 map에 기록
				int chatRoomLastMessageSeq = cMDao.lastMessageSeq(checkChat);
				int myLastMessageSeq = dao.getLastMessageSeq(dto.getEmail(), checkChat);
				// 내가 마지막으로 읽은 메세지보다 더 신규 메세지가 있다면
				map.put("alert", myLastMessageSeq < chatRoomLastMessageSeq ? "y" : "");
				map.put("chat_seq", checkChat);
				map.put("level_code", members.getLevel_code());
				map.put("name", members.getName());
			}else {
				// 채팅방 생성 후 seq 뽑아서 방생성 후 정보 반환
				Chat_roomDTO Chat_roomDTO = new Chat_roomDTO();
				Chat_roomDTO.setManager_email(dto.getEmail());
				String name = memberDao.selectMemberName(dto.getEmail());
				String memberName = memberDao.selectMemberName(members.getEmail());
				Chat_roomDTO.setChat_name(name+memberName);
				int chatSeq = roomDao.insertPirvateCahtRoom(Chat_roomDTO);
				// chat_member 테이블에 사원 insert
				Chat_memberDTO manager = new Chat_memberDTO();
				manager.setChat_seq(chatSeq);
				manager.setMember_email(dto.getEmail());
				manager.setRole("manager");
				dao.insertCahtMember(manager);
				// chat_member 테이블에 같은 부서 직원 insert 
				Chat_memberDTO member = new Chat_memberDTO();
				member.setChat_seq(chatSeq);
				member.setMember_email(members.getEmail());
				member.setRole("general");
				dao.insertCahtMember(member);
				// 정보 포장
				map.put("alert", "");
				map.put("chat_seq", chatSeq);
				map.put("level_code", members.getLevel_code());
				map.put("name", members.getName());
			}
			list.add(map);
		}
		return list;
	}

	// 부서 단체 톡방 생성 및 단체 톡방 출력
	public List<Map<String, Object>> chatRoomList(MemberDTO dto){
		// 내 부서명 출력
		String department = memberDao.selectDepartment(dto);
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		// 팀 채팅방 있는지 확인 ( 존재 한다면 room_seq / 존재하지 않는다면 0 )
		int checkChat = roomDao.searchRoom(department+" 단체 채팅");
		if(checkChat > 0) {
			int exist = dao.existDepartmentRoom(dto, checkChat);
			// 단톡방에 내가 없을시 참여
			if(exist == 0) {
				Chat_memberDTO members = new Chat_memberDTO();
				members.setChat_seq(checkChat);
				members.setMember_email(dto.getEmail());
				members.setRole("general");
				dao.insertCahtMember(members);
			}
			int chatRoomLastMessageSeq = cMDao.lastMessageSeq(checkChat);
			int myLastMessageSeq = dao.getLastMessageSeq(dto.getEmail(), checkChat);
			// 내가 마지막으로 읽은 메세지보다 더 신규 메세지가 있다면
			map.put("alert", myLastMessageSeq < chatRoomLastMessageSeq ? "y" : "");
			map.put("chat_seq", checkChat);
			map.put("dept_code", department);
			map.put("chat_name", department+" 단체 채팅");
		}else {
			// 같은 부서의 팀 정보 출력
			List<MemberDTO> memberList = memberDao.memberSearch(dto);
			Chat_roomDTO chatRoomDTO = new Chat_roomDTO();
			chatRoomDTO.setManager_email(dto.getEmail());;
			chatRoomDTO.setChat_name(department+" 단체 채팅");
			// 생성한 채팅방 생성 후 시퀀스 값 받기
			int chatSeq = roomDao.insetDepartmentRoom(chatRoomDTO);
			// 첫 생성자(해당유저) chat 테이블에 insert
			Chat_memberDTO manager = new Chat_memberDTO();
			manager.setChat_seq(chatSeq);
			manager.setMember_email(dto.getEmail());
			manager.setRole("manager");
			dao.insertCahtMember(manager);
			// 같은 부서 팀도 chat 테이블에 insert
			for(MemberDTO members : memberList) {
				Chat_memberDTO member = new Chat_memberDTO();
				member.setChat_seq(chatSeq);
				member.setMember_email(members.getEmail());
				member.setRole("general");
				dao.insertCahtMember(member);
			}
			map.put("alert", "");
			map.put("dept_code", department);
			map.put("chat_seq", chatSeq);
			map.put("chat_name", department+" 단체 채팅");
		}
		list.add(map);
		// 내가 참여하고있는 단톡방 서치 (같은 부서제외)
		List<Map<String, Object>> myChats = roomDao.selectChatRoom(dto, department);
		System.out.println(myChats);
		// 회사 단체 채팅은 제외하고 위에서 했으니까
		for(Map<String, Object> chat : myChats) {
			int memberCount = dao.memberCount(chat.get("CHAT_SEQ").toString());
			if(memberCount <= 2) {
				String chatName = (String) chat.get("CHAT_NAME");
				// 사원 이름 출력
				String username = memberDao.selectMemberName(dto.getEmail());
				// 채팅방 이름에서 사원 이름을 제거후 저장
				String cleanedName = chatName.replaceAll(username, "");
				chat.put("CHAT_NAME", cleanedName);
			}
			if(!chat.get("CHAT_NAME").equals(department + " 단체 채팅")) {
				list.add(chat);
			}
			chat.put("alert", "");
			chat.put("chat_seq", chat.get("CHAT_SEQ"));
			chat.put("chat_name", chat.get("CHAT_NAME"));
			chat.remove("CHAT_NAME");chat.remove("CHAT_SEQ");
		}
		return list;
	}

	// 종료된 프로젝트 채팅방 출력
	public List<Map<String, Object>> completedList(MemberDTO dto){
		List<Chat_roomDTO> chatList = roomDao.completedList(dto);
		List<Map<String, Object>> list = new ArrayList<>();
		for(Chat_roomDTO chatroom : chatList) {
			Map<String, Object> map = new HashMap<>();
			map.put("chat_seq", chatroom.getChat_seq());
			map.put("chat_name", chatroom.getChat_name());
			list.add(map);
		}
		return list;
	}

	// 부서원 제외 개인 채팅방 정보 출력
	public Map<String, Object> ChatRoom(Chat_memberDTO dto) {
		// 채팅방 정보 출력
		Map<String, Object> list = roomDao.chatRoom(dto);
		// 디비에서 데이터 자체를 타입안정하고 끄내와서 이거해줘야한데 몰라 
		BigDecimal memberCount = (BigDecimal) list.get("MEMBER_COUNT");
		// 만약 개인톡방이라면
		if(memberCount.intValue() == 2) {
			String chatName = (String) list.get("CHAT_NAME");
			// 사원 이름 출력
			String username = memberDao.selectMemberName(dto.getMember_email());
			// 채팅방 이름에서 사원 이름을 제거후 저장
			String cleanedName = chatName.replaceAll(username, "");
			list.put("CHAT_NAME", cleanedName);
		}
		return list;
	}

	// 채널 추가 주소록 출력 
	public List<ContactDTO> contactList(Chat_memberDTO dto){
		String eamil = dto.getMember_email();
		List<ContactDTO> list = ContactDAO.contactList(eamil);
		list.removeIf(l -> memberDao.checkMember(l.getEmail()) == 0);
		return list;
	}

	// 채널 추가
	public int newCaht(String ownerEmail, String title, List<Object> contactSeq) {
		List<String> memberList = new ArrayList<>();
		for(Object list : contactSeq) {
			int contact_seq = (int)list;
			String memberEmail =  ContactDAO.selectName(contact_seq);
			memberList.add(memberEmail);
		}
		// 채팅방 생성 후 채팅방 seq 반환
		Chat_roomDTO dto = new Chat_roomDTO();
		dto.setChat_name(title); // 나중에 받아야함
		dto.setManager_email(ownerEmail);
		int chatSeq = roomDao.insertPirvateCahtRoom(dto);
		// chat_member insert (생성자)
		Chat_memberDTO manager = new Chat_memberDTO();
		manager.setChat_seq(chatSeq);
		manager.setMember_email(ownerEmail);
		manager.setRole("manager");
		dao.insertCahtMember(manager);
		// chat_member insert (초대받은인원)
		for(String member : memberList) {
			Chat_memberDTO chat_memberDTO = new Chat_memberDTO();
			chat_memberDTO.setChat_seq(chatSeq);
			chat_memberDTO.setMember_email(member);
			chat_memberDTO.setRole("general");
			dao.insertCahtMember(chat_memberDTO);
		}
		return chatSeq;
	}

}
