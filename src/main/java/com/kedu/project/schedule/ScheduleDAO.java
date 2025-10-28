package com.kedu.project.schedule;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kedu.project.chatting.chat_member.Chat_memberDTO;
import com.kedu.project.members.member.MemberDTO;

/*
	먼슬리 ( 일정 테이블 ) 기능 구현 DAO
*/
@Repository
public class ScheduleDAO {
	@Autowired
	private SqlSession mybatis;
	
	// 같은 채팅방 멤버 목록 출력
	public List<MemberDTO> selectMember(Chat_memberDTO dto){
		return mybatis.selectList("Schedule.selectMember", dto);
	}
	
	// 새로운 이벤트 DB 저장
	public int sevaEvent(ScheduleDTO dto) {
		return mybatis.insert("Schedule.savaEvent", dto);
	}
	
	// 일정 목록 뽑아서 전달
	public List<ScheduleDTO> eventsList(ScheduleDTO dto){
		return mybatis.selectList("Schedule.eventsList", dto);
	}
	
	// 채팅 멤버중 일정 제외 후 출력
	public List<ScheduleDTO> eventsListByEmail(ScheduleDTO dto, List<String> selectedEmails){
		return mybatis.selectList("Schedule.eventsListByEmail", Map.of(
				"chat_seq", dto.getChat_seq(),
				"excludeEmails", selectedEmails));
	}
	
	// 일정 삭제
	public int deleteEvent(ScheduleDTO dto) {
		return mybatis.delete("Schedule.deleteEvent", dto);
	}
	
}
