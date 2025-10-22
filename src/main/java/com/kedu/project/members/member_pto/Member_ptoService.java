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
	
	//이메일로 남은 연차 가져오는 로직
	public Member_ptoDTO getLeftPto(String member_email) {
		return dao.getLeftPto(member_email);
	}
	
	//연차별로 연차 증정
	public int updatePto(String member_email, int updatedPto) {
		Map <String, Object> param = new HashMap<>();
		param.put("member_email", member_email);
		param.put("remaining_pto", updatedPto);
		
		return dao.updatePto(param);
	}
	
	
	
	public int calculateAnnualPtoByEmail(String member_email) {
	    MemberDTO member = daoMember.findByEmail(member_email);
	    if (member == null || member.getSignup_at() == null) return 0;

	    LocalDate signupAt = member.getSignup_at().toLocalDateTime().toLocalDate();
	    LocalDate today = LocalDate.now();
	    int years = Period.between(signupAt, today).getYears();

	    // 오늘이 정확히 근속 기념일인지 확인
	    LocalDate anniversary = signupAt.plusYears(years);
	    if (!today.isEqual(anniversary)) return 0;

	    // 지급 로직
	    if (years < 1) return 11;
	    return Math.min(15 + (years - 1) / 2, 25);
	}
	
	public int standardPto(String member_email) {
	    MemberDTO member = daoMember.findByEmail(member_email);
	    if (member == null || member.getSignup_at() == null) return 0;

	    LocalDate signupAt = member.getSignup_at().toLocalDateTime().toLocalDate();
	    LocalDate today = LocalDate.now();
	    int years = Period.between(signupAt, today).getYears();

	    // 지급 로직
	    if (years < 1) return 11;
	    return Math.min(15 + (years - 1) / 2, 25);
	}
	


	
	// 매일 12:00정각에 연차업데이트 로직
	@Scheduled(cron = "0 0 0 * * *")
	//@Scheduled(cron = "0 58 15 * * *")
	public void grantAnnualPto() {
	    List<MemberDTO> members = daoMember.findAll();

	    for (MemberDTO m : members) {
	        try {
	            String email = m.getEmail();
	            int expectedPto = calculateAnnualPtoByEmail(email);

	            if (expectedPto > 0) {
	                Member_ptoDTO ptoInfo = dao.getLeftPto(email);
	                int currentPto = (ptoInfo != null) ? ptoInfo.getRemaining_pto() : 0;

	                this.updatePto(email, expectedPto + currentPto);
	            }
	        } catch (Exception e) {
	            // 로깅만 하고 루프 계속
	            System.err.println("연차 지급 실패: " + m.getEmail() + " → " + e.getMessage());
	        }
	    }
	}


	
	
	
}
