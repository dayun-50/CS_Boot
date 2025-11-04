package com.kedu.project.chatting.chat_room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.chatting.chat_member.Chat_memberDTO;

import jakarta.servlet.http.HttpServletRequest;



/*
	채팅방 생성 및 프로젝트 on off 기능 구현 Controller
*/
@RequestMapping("/chatRoom")
@RestController
public class Chat_roomController {
	@Autowired
	private Chat_roomService Chat_roomService;
	
	// 방 생성자(매니저) 이메일 출력
	@PostMapping("/manager")
	public ResponseEntity<Chat_roomDTO> selectChatRoomManager(@RequestBody Chat_roomDTO dto){
		Chat_roomDTO managerEmail = Chat_roomService.selectChatRoomManager(dto);
		return ResponseEntity.ok(managerEmail);
	}

	// 채팅방 on /off 
	@PostMapping("/projectOnOff")
	public ResponseEntity<?> updateProjectOnOff(@RequestBody Chat_roomDTO dto, HttpServletRequest request){
		String email = (String) request.getAttribute("email");
		int result = Chat_roomService.updateProjectOnOff(dto, email);
		return ResponseEntity.ok(result);
	}
	
	// 채팅방 나가기
	@PostMapping("/outChat")
	public ResponseEntity<?> outChat(@RequestBody Chat_memberDTO dto, HttpServletRequest request){
		String email = (String) request.getAttribute("email");
		dto.setMember_email(email);
		Chat_roomService.outChat(dto);
		return ResponseEntity.ok("성공");
	}
}
