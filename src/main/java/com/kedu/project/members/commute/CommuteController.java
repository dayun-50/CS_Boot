package com.kedu.project.members.commute;


import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *  	근태관리 기능 구현 Controller
 * */

@RequestMapping("/workhour")
@RestController
public class CommuteController {
	@Autowired
	private CommuteService commuteService;
	
	//페이지 들어갔을때 데이터 가져오기
	@GetMapping()
	public ResponseEntity<CommuteDTO> getTodayData(){
		String member_email = "test@test.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
		LocalDate today = LocalDate.now();// 현재 날짜 가져오기
		CommuteDTO todayRecord = commuteService.getCommuteByDate(member_email, today);// 이메일과 현재 날짜 보내서 dto 꺼내오기
		
	    if (todayRecord != null) {// 1. 오늘 출근 기록 있음 → 그대로 반환
	        return ResponseEntity.ok(todayRecord);
	    }
	    
	    // 2. 오늘 출근 기록 없음 → 가장 최근 출근 기록 조회
	    CommuteDTO latestRecord = commuteService.getLatestCommute(member_email);//이메일 보내서 가장 최신날짜 데이터 뽑아오기
	    if (latestRecord != null && latestRecord.getLeave_at() == null) {//데이터는 존재 + 해당 데이터의 퇴근이 아직 안찍혀 있다면
	    	return ResponseEntity.ok(latestRecord);
	    }
	    
	    //3.  출근도 안 했고, 퇴근도 완료된 상태 → 출근 버튼만 활성화
		return ResponseEntity.ok(new CommuteDTO());
	}
	
	//시작시간 입력
	@PostMapping("/start")
	public ResponseEntity<Void> inputStart(@RequestBody Map<String, String> param) {
	    String member_email = "test@test.com"; // 추후 토큰에서 추출 예정
	    String workAtStr = param.get("work_at");

	    if (workAtStr == null) {
	        return ResponseEntity.badRequest().build();
	    }
	    commuteService.inputStart(member_email, workAtStr);// 서비스에서 문자열 → Timestamp, localdate 변환 및 출근 처리
	    return ResponseEntity.ok().build();
	}

	
	//퇴근시간 입력
	@PostMapping("/end")
	public ResponseEntity<Void> inputEnd(@RequestBody Map<String, String> param){
	    String member_email = "test@test.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	    String workAtStr = param.get("work_at");
	    String leavAtStr = param.get("leave_at");
	    System.out.println(leavAtStr);
	    if (workAtStr == null || leavAtStr==null) {//빈 파라미터 방지용
	        return ResponseEntity.badRequest().build();
	    }

	    commuteService.inputEnd(member_email, workAtStr, leavAtStr);
	    return ResponseEntity.ok().build();
	}	
	
	//위클리 토탈 시간 뽑아오기
	@GetMapping("/weekly")
	public ResponseEntity<Integer> getWeeklyTotalMin(){
		String member_email = "test@test.com"; // 추후 토큰에서 추출 예정
		LocalDate today = LocalDate.now(); // 오늘 날짜
		// LocalDate today = LocalDate.of(2025, 10, 17);임의 날짜 설정: 2025년 10월 17일
	    LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1); // 이번 주 월요일		
		int totalMinutes = commuteService.getWeeklyTotalMin(member_email,today,monday);

		return ResponseEntity.ok(totalMinutes);
	}
	
	//달별 이슈들 뽑아오기
	@GetMapping("/issue")
	public ResponseEntity<Map<String, Object>> getMonthlyIssue(){
		String member_email = "test@test.com"; // 추후 토큰에서 추출 예정
		LocalDate today = LocalDate.now(); // 오늘 날짜
		LocalDate startOfMonth = today.withDayOfMonth(1);
		
		Map<String, Object> param = commuteService.getMonthlyIssue(member_email, today, startOfMonth);
		return ResponseEntity.ok(param);
	} 
	
	
}
