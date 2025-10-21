package com.kedu.project.chatting.chat_member;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.members.member.MemberDTO;


/*
	채팅방 참여자 관리 controller
 */
@RequestMapping("/chat")
@RestController
public class Chat_memberController {
	@Autowired
	private Chat_memberService Chat_memberService;

	// 팀원간 개인 메세지 출력 
	@PostMapping("/private")
	public ResponseEntity<List<Map<String, Object>>> privateChat(@RequestBody MemberDTO dto) {
		List<Map<String, Object>> list = Chat_memberService.privatChatSearch(dto);
		return ResponseEntity.ok(list);
	}
	
	@PostMapping("/chatRoom")
	public ResponseEntity<List<Map<String, Object>>> chatRoom(@RequestBody MemberDTO dto){
		List<Map<String, Object>> list = Chat_memberService.chatRoom(dto);
		return ResponseEntity.ok(list);
	}
}
