package com.kedu.project.members.member_pto;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/*
 * 		남은 연차 관리 service
 * */

import com.kedu.project.approval.ApprovalDAO;
import com.kedu.project.members.member.MemberDAO;
import com.kedu.project.members.member.MemberDTO;

@Service
public class Member_ptoService {
	@Autowired
	private Member_ptoDAO dao;
	@Autowired
	private MemberDAO daoMember;	
	
	//0.초기값 넣기
	public int insertInitPto(String member_email) {
		return dao.insertInitPto(member_email);
	}
	
	//1. 이메일로 남은 연차 가져오는 로직
	public Member_ptoDTO getLeftPto(String member_email) {
		return dao.getLeftPto(member_email);
	}
	
	// 2. 특정 회원의 잔여 연차를 갱신하는 메서드 (DB의 연차 값을 직접 업데이트)
	public int updatePto(String member_email, double updatedPto) {
		Map <String, Object> param = new HashMap<>();
		param.put("member_email", member_email);
		param.put("remaining_pto", updatedPto);
		
		return dao.updatePto(param);
	}
	
	
	// 2️-1. 특정 회원의 "오늘"이 근속 1년이 지나면 → 지급할 연차 계산
	public int calculateAnnualPtoByEmail(String member_email) {
	    MemberDTO member = daoMember.findByEmail(member_email);
	    if (member == null || member.getSignup_at() == null) return 0;

	    LocalDate signupAt = member.getSignup_at().toLocalDateTime().toLocalDate();
	    LocalDate today = LocalDate.now();
	    int years = Period.between(signupAt, today).getYears();//오늘과 입사일 사이의 지난 연도를 구함

	    // 올해 근속기념일 (입사일 + years)
	    LocalDate anniversary = signupAt.plusYears(years);

	    //오늘이 기념일 이전이면 지급 안 함 or 오늘이 기념일이거나(정상) 이미 지났으면(지각 지급) 지급 허용
	    if (today.isBefore(anniversary)) return 0;

	    // 지급 규칙
	    if (years < 1) return 11;
	    return Math.min(15 + (years - 1) / 2, 25);
	}
	
//	public int calculateAnnualPtoByEmail(String member_email) {
//	    MemberDTO member = daoMember.findByEmail(member_email);
//	    if (member == null || member.getSignup_at() == null) return 0;
//
//	    LocalDate signupAt = member.getSignup_at().toLocalDateTime().toLocalDate();
//	    LocalDate today = LocalDate.now();
//	    int years = Period.between(signupAt, today).getYears();
//
//	    // 오늘이 정확히 근속 기념일인지 확인
//	    LocalDate anniversary = signupAt.plusYears(years);
//	    if (!today.isEqual(anniversary)) return 0;
//
//	    // 지급 규칙
//	    if (years < 1) return 11; // 1년 미만은 입사 1년차 지급분 (11개)
//	    return Math.min(15 + (years - 1) / 2, 25);// 2년차부터는 2년에 1개씩 증가, 최대 25개
//	}
	
	
	//2-2.“기준 연차”만 단순 계산(UI용)
	public int standardPto(String member_email) {
	    MemberDTO member = daoMember.findByEmail(member_email);
	    if (member == null || member.getSignup_at() == null) return 0;

	    LocalDate signupAt = member.getSignup_at().toLocalDateTime().toLocalDate();
	    LocalDate today = LocalDate.now();
	    int years = Period.between(signupAt, today).getYears();

	    // 지급 로직
	    if (years < 1) return 11;// 1년 미만은 입사 1년차 지급분 (11개)
	    return Math.min(15 + (years - 1) / 2, 25);// 2년차부터는 2년에 1개씩 증가, 최대 25개
	}
	

	// 3. 매일 12:00정각에 계산: 입사일로부터 1년단위로 연차업데이트해주는 로직
	@Scheduled(cron = "0 0 0 * * *")
	public void grantAnnualPto() {
	    List<MemberDTO> members = daoMember.findAll();

	    for (MemberDTO m : members) {
	        try {
	            String email = m.getEmail();
	            double expectedPto = calculateAnnualPtoByEmail(email);
	            if (expectedPto <= 0) continue;//입사일이 안지낫으면 skip

	            Member_ptoDTO ptoInfo = dao.getLeftPto(email);
	            if (ptoInfo == null) continue;

	            double currentPto = ptoInfo.getRemaining_pto();//현재 연차값 가져오기
	            LocalDate lastGranted = ptoInfo.getLast_granted_at().toLocalDateTime().toLocalDate();//마지막 지급일 가져오고
	            LocalDate today = LocalDate.now();//현재 날자 가져와서

	            LocalDate signupAt = m.getSignup_at().toLocalDateTime().toLocalDate();
	            LocalDate anniversary = signupAt.plusYears(Period.between(signupAt, today).getYears());

	            // 올해 아직 지급 안 했고, 기념일 이후 첫날이면만 지급
	            if (lastGranted.isBefore(anniversary) && !today.isBefore(anniversary)) {
	                this.updatePto(email, currentPto + expectedPto);
	                System.out.println("[연차 지급 완료] " + email + " → +" + expectedPto + "일 지급 (" + today + ")");
	            }

	        } catch (Exception e) {
	            System.err.println("연차 지급 실패: " + m.getEmail() + " → " + e.getMessage());
	        }
	    }
	}
	
	
	
//	@Scheduled(cron = "0 21 16 * * *")
//	public void grantAnnualPto() {
//	    List<MemberDTO> members = daoMember.findAll(); // 전체 회원 조회
//
//	    for (MemberDTO m : members) {
//	        try {
//	            String email = m.getEmail();
//	            double expectedPto = calculateAnnualPtoByEmail(email);
//
//	            if (expectedPto > 0) {// 지급할 연차가 있다면
//	                Member_ptoDTO ptoInfo = dao.getLeftPto(email);
//	                double currentPto = (ptoInfo != null) ? ptoInfo.getRemaining_pto() : 0;
//	                this.updatePto(email, expectedPto + currentPto);// 기존 잔여 연차에 새로 지급분을 더해 DB 업데이트
//	            }
//	        } catch (Exception e) {
//	        	// 한 명 실패해도 다른 사람 처리 계속
//	            System.err.println("연차 지급 실패: " + m.getEmail() + " → " + e.getMessage());
//	        }
//	    }
//	}


	
	
	
}
