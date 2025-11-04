package com.kedu.project.chatting.chat_member;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.contact.ContactDTO;
import com.kedu.project.members.member.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;


/*
	채팅방 참여자 관리 controller
 */
@RequestMapping("/chat")
@RestController
public class Chat_memberController {
	@Autowired
	private Chat_memberService Chat_memberService;

	// 팀원간 개인 메세지방 출력 
	@PostMapping("/private")
	public ResponseEntity<List<Map<String, Object>>> privateChat(HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		List<Map<String, Object>> list = Chat_memberService.privatChatSearch(email);
		return ResponseEntity.ok(list);
	}

	// 부서 단체 톡방 생성 및 단체 톡방 출력
	@PostMapping("/chatRoomList")
	public ResponseEntity<List<Map<String, Object>>> chatRoomList(HttpServletRequest request){
		String email = (String) request.getAttribute("email");
		List<Map<String, Object>> list = Chat_memberService.chatRoomList(email);
		return ResponseEntity.ok(list);
	}

	// 종료된 프로젝트 채팅방 출력
	@PostMapping("/completedList")
	public ResponseEntity<List<Map<String, Object>>> completedList(HttpServletRequest request){
		String email = (String) request.getAttribute("email");
		List<Map<String, Object>> list = Chat_memberService.completedList(email);
		return ResponseEntity.ok(list);
	}

	// 부서원 제외 개인 채팅방 정보 출력
	@PostMapping("/chatRoom")
	public ResponseEntity<Map<String, Object>> chatRoom(@RequestBody Chat_memberDTO dto, HttpServletRequest request){
		String email = (String) request.getAttribute("email");
		dto.setMember_email(email);
		Map<String, Object> chatRoom = Chat_memberService.ChatRoom(dto);
		return ResponseEntity.ok(chatRoom);
	}

	// 채널 추가 주소록 출력
	@PostMapping("/contactList")
	public ResponseEntity<List<ContactDTO>> contactList(HttpServletRequest request){
		String email = (String) request.getAttribute("email");
		List<ContactDTO> list = Chat_memberService.contactList(email);
		return ResponseEntity.ok(list);
	}
	
	@PostMapping("/newCaht")
	public ResponseEntity<?> newChat(HttpServletRequest request, @RequestBody Map<String, Object> param) {
	    String email = (String) request.getAttribute("email");
	    String title = (String) param.get("title");
	    List<Object> contactSeq = (List<Object>) param.get("contact_seq");
	    int chatSeq = Chat_memberService.newCaht(email, title, contactSeq);

	    return ResponseEntity.ok(chatSeq);
	}
	
}
