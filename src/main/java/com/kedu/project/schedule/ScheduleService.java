package com.kedu.project.schedule;

import java.sql.Timestamp;
import java.util.List;

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
	public List<ScheduleDTO> eventsList(ScheduleDTO dto){
		return dao.eventsList(dto);
	}
}
