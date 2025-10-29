package com.kedu.project.schedule;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.chatting.chat_member.Chat_memberDTO;
import com.kedu.project.members.member.MemberDAO;
import com.kedu.project.members.member.MemberDTO;

/*
먼슬리 ( 일정 테이블 ) 기능 구현 service
 */

@Service
public class ScheduleService {
	@Autowired
	private ScheduleDAO dao;

	@Autowired
	private MemberDAO memberDao;

	// 같은 채팅방 멤버 목록 출력
	public List<MemberDTO> selectMember(Chat_memberDTO dto) {
		return dao.selectMember(dto);
	}

	// 새로운 이벤트 DB 저장
	public int sevaEvent(ScheduleDTO dto) {
		Timestamp start_at = dto.getStart_at();
		Timestamp end_at = dto.getEnd_at();
		long all_day = end_at.getTime() - start_at.getTime();
		// 날짜가 다음날기준 00:00 으로 들어가서 24시간 이상시 종일여부 o
		dto.setAll_day(all_day > 24 * 60 * 60 * 1000 ? "y" : "n");
		String name = memberDao.selectMemberName(dto.getMember_email());
		dto.setTitle(name+" : "+dto.getTitle());
		return dao.sevaEvent(dto);
	}

	// 일정 목록 뽑아서 전달
	public List<Map<String, Object>> eventsList(ScheduleDTO dto, List<String> selectedEmails){
		List<ScheduleDTO> events;
		if(selectedEmails == null) { // 제외할 member가없을시 dto만전달
			events = dao.eventsList(dto);
		}else { // 제외할 member가 있을시 포함해서 전달
			events = dao.eventsListByEmail(dto, selectedEmails);
		}
		// stream 사용으로 연산 수행가능하게 바꿔서
		List<Map<String, Object>> result = events.stream()
				.map(e -> { // 리액트의 그 map 맞음 한개씩 e라는 객체로 끄내서 연산 수행
					Map<String, Object> m = new HashMap<>();
					m.put("member_email", e.getMember_email());
					m.put("schedule_seq", e.getSchedule_seq());
					m.put("title", e.getTitle());
					m.put("start", e.getStart_at().toInstant().toString()); // ISO 문자열
					m.put("end", e.getEnd_at().toInstant().toString());
					m.put("color", e.getColor());
					m.put("allDay", "y".equals(e.getAll_day()));
					return m;
				})
				.collect(Collectors.toList());
		return result;
	}
	
	// 일정 삭제
	public int deleteEvent(ScheduleDTO dto) {
		return dao.deleteEvent(dto);
	}
	
	// 마이페이지 일정출력
	public List<ScheduleDTO> selectMySchedule(ScheduleDTO dto){
		List<ScheduleDTO> list = dao.selectMySchedule(dto);
		for(ScheduleDTO l : list) {
			String name = memberDao.selectMemberName(l.getMember_email());
			l.setMember_email(name);
		}
		return list;
	}
}
