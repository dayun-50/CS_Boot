package com.kedu.project.members.commute;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		근태관리 기능 구현 DAO
 * */

@Repository
public class CommuteDAO {
	@Autowired
	private SqlSession mybatis;
	
	
	//오늘 날짜로 데이터 조회
	public CommuteDTO getCommuteByDate (Map<String, Object> param) {
		return mybatis.selectOne("Commute.getCommuteByDate", param);
	}
	
	//최신 날짜로 데이터 조회
	public CommuteDTO getLatestCommute (String member_email) {
		return mybatis.selectOne("Commute.getLatestCommute", member_email);
	}	
	
	//출근시간 입력
	public int insertCommute (CommuteDTO dto) {
		return mybatis.insert("Commute.insertCommute", dto);
	}
	
	//퇴근시간 입력
	public int inputEnd (Map<String, Object> param) {
		System.out.println(param.get("leave_at"));
		return mybatis.update("Commute.inputEnd",param);
	}
	
	//입력받은 날짜에 해당하는 주의 총 근무시간 뽑아오기
	public int getWeeklyTotalMin (Map<String, Object> param) {
		Integer result =mybatis.selectOne("Commute.getWeeklyTotalMin", param);
		System.out.println("db에서 꺼내온 분"+result);
		return result != null ? result : 0;
	}
	
	//입력받은 날짜에 해당하는 달의 이슈 뽑아오기
	public Map<String, Object> getMonthlyIssue(Map<String, Object> param) {
	    return mybatis.selectOne("Commute.getMonthlyIssue", param);
	}

	
	
}
