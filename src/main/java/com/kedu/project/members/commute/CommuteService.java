package com.kedu.project.members.commute;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
 *  	근태관리 기능 구현 Service
 * */

@Service
public class CommuteService {
	@Autowired
	private CommuteDAO dao;
	
	// ISO 문자열 → LocalDateTime
	private LocalDateTime toLocalDateTime(String isoString) {
	    return OffsetDateTime.parse(isoString).toLocalDateTime();
	}

	// LocalDateTime → Timestamp
	private Timestamp toTimestamp(LocalDateTime ldt) {
	    return Timestamp.valueOf(ldt);
	}

	// 조기 퇴근 판단
	private boolean isLeaveEarly(LocalDateTime leaveAt) {
	    return leaveAt.toLocalTime().isBefore(LocalTime.of(18, 0)); // 18:00 이전이면 조퇴
	}

	// 지각 판단
	private boolean isLate(LocalDateTime workAt) {
	    return workAt.toLocalTime().isAfter(LocalTime.of(9, 0)); // 9:00 초과면 지각
	}

	
	//오늘 날짜로 데이터 조회
	public CommuteDTO getCommuteByDate(String member_email, LocalDate today) {
		Map<String, Object> param = new HashMap<>();
		param.put("member_email", member_email);
		param.put("commute_at", today);
		return dao.getCommuteByDate(param);
	}
	
	//최신 날짜로 데이터 조회
	public CommuteDTO getLatestCommute(String member_email) {
		return dao.getLatestCommute(member_email);
	}	
	
	//출근시간 입력
	public int inputStart(String memberEmail, String workAtStr) {//ISO 스트링 값으로 받아서
	    LocalDateTime workAtLdt = toLocalDateTime(workAtStr); //로컬데이트 타입으로 바꿈
	    Timestamp workAt = toTimestamp(workAtLdt);// 타임스트탬프로 바꾸기
	    LocalDate commuteAt = workAtLdt.toLocalDate();//로컬데이트 타입에서 데이트까지만 뽑기

	    CommuteDTO dto = new CommuteDTO();
	    dto.setMember_email(memberEmail);
	    dto.setWork_at(workAt);
	    dto.setCommute_at(commuteAt);
	    dto.setLateness(isLate(workAtLdt) ? "y" : "n");
	    dto.setAbsence("출근");

	    return dao.insertCommute(dto);
	}

	//퇴근시간 입력
	public int inputEnd(String member_email, String workAtStr, String leavAtStr) {
	    LocalDateTime workAtLdt = toLocalDateTime(workAtStr); //로컬데이트 타입으로 바꿈
	    LocalDateTime leaveAtLdt = toLocalDateTime(leavAtStr); //로컬데이트 타입으로 바꿈
	    Timestamp leaveAt = toTimestamp(leaveAtLdt);// 타임스트탬프로 바꾸기
	    Timestamp workAt = toTimestamp(workAtLdt);// 타임스트탬프로 바꾸기
	    LocalDate commuteAt = workAtLdt.toLocalDate();//로컬데이트 타입에서 데이트까지만 뽑기
	    
	    Map <String, Object> param = new HashMap<>();
	    param.put("member_email", member_email);
	    param.put("work_at", workAt);
	    param.put("leave_at",leaveAt);
	    param.put("commute_at",commuteAt);
	    param.put("leave_early", isLeaveEarly(leaveAtLdt)? "y" : "n");
	    
	    System.out.println(leaveAt);
	    
	    return dao.inputEnd(param);
	}
	
	//입력받은 날짜에 해당하는 주의 총 근무시간 뽑아오기
	public int getWeeklyTotalMin(String member_email, LocalDate today, LocalDate monday) {
		Map <String, Object> param= new HashMap<>();
		param.put("member_email", member_email);
		param.put("end_date", today);
		param.put("start_date", monday);		
		
		return dao.getWeeklyTotalMin(param);
	}
	
	//입력받은 날짜에 해당하는 달의 이슈 뽑아오기
	public Map<String, Object> getMonthlyIssue(String member_email, LocalDate today, LocalDate startOfMonth){
		Map <String, Object> param= new HashMap<>();
		param.put("member_email", member_email);
		param.put("end_date", today);
		param.put("start_date", startOfMonth);		
		
		return dao.getMonthlyIssue(param);
		
		
	}
	
}
