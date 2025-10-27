package com.kedu.project.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ResponseEntity<List<Map<String, Object>>> eventsList(@RequestBody ScheduleDTO dto) {
		List<ScheduleDTO> events = ScheduleService.eventsList(dto);
		// stream 사용으로 연산 수행가능하게 바꿔서
		List<Map<String, Object>> result = events.stream()
			    .map(e -> { // 리액트의 그 map 맞음 한개씩 e라는 객체로 끄내서 연산 수행
			        Map<String, Object> m = new HashMap<>();
			        m.put("title", e.getTitle());
			        m.put("start", e.getStart_at().toInstant().toString()); // ISO 문자열
			        m.put("end", e.getEnd_at().toInstant().toString());
			        m.put("color", e.getColor());
			        m.put("allDay", "y".equals(e.getAll_day()));
			        return m;
			    })
			    .collect(Collectors.toList());

		return ResponseEntity.ok(result);
	}
}
