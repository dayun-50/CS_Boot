package com.kedu.project.members.member_pto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		남은 연차 관리 DAO
 * */

@Repository
public class Member_ptoDAO {
	@Autowired
	private SqlSession mybatis;

	//남은 연차 가져오는 로직
	public Member_ptoDTO getLeftPto(String member_email) {
		return mybatis.selectOne("Member_pto.getLeftPto", member_email);
	}
	
	//회원가입 시기에 초기 연차 인서트하는 로직
	public int insertInitPto(String member_email) {
		return mybatis.insert("Member_pto.insertInitPto", member_email);		
	}
	
	//연차 업데이트
	public int updatePto(Map <String, Object> param) {
		return mybatis.update("Member_pto.updatePto", param);			
	}
	
	


}
