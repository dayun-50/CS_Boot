package com.kedu.project.schedule;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.chatting.chat_member.Chat_memberDTO;
import com.kedu.project.members.member.MemberDTO;

/*
먼슬리 ( 일정 테이블 ) 기능 구현 controller
 */

@RequestMapping("/schedule")
@RestController
public class ScheduleController {
	@Autowired
	private ScheduleService ScheduleService;

	// 같은 채팅방 멤버 목록 출력
	@PostMapping("/selectMember")
	public ResponseEntity<List<MemberDTO>> selectMember(@RequestBody Chat_memberDTO dto){
		List<MemberDTO> list = ScheduleService.selectMember(dto);
		System.out.println(list);
		return ResponseEntity.ok(list);
	}

	// 새로운 이벤트 DB 저장
	@PostMapping("/sevaEvent")
	public ResponseEntity<?> sevaEvent(@RequestBody ScheduleDTO dto){
		ScheduleService.sevaEvent(dto);
		return ResponseEntity.ok(null);
	}

	// 일정 목록 뽑아서 전달
	@PostMapping("eventsList")
	public ResponseEntity<List<Map<String, Object>>> eventsList(@RequestBody ScheduleDTO dto,
			@RequestParam(required = false) List<String> selectedEmails) {
		List<Map<String, Object>> result = ScheduleService.eventsList(dto, selectedEmails);
		return ResponseEntity.ok(result);
	}
	
	// 일정 삭제
	@PostMapping("/deleteEvent")
	public ResponseEntity<?> deleteEvent(@RequestBody ScheduleDTO dto){
		ScheduleService.deleteEvent(dto);
		return ResponseEntity.ok(null);
	}
	
	// 마이페이지 일정출력
	@PostMapping("/myschedule")
	public ResponseEntity<List<ScheduleDTO>> selectMySchedule(@RequestBody ScheduleDTO dto){
		List<ScheduleDTO> list = ScheduleService.selectMySchedule(dto);
		System.out.println(dto.getMember_email());
		return ResponseEntity.ok(list);
	}
}
