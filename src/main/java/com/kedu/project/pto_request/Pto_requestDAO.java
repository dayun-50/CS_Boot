package com.kedu.project.pto_request;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



/*
 * 		연차 신청 기능 관련 DAO
 * */

@Repository
public class Pto_requestDAO {
	@Autowired
	private SqlSession mybatis;
	
	// 전체 타입의 데이터 개수
	public int getCount(String member_email) {
		return mybatis.selectOne("PtoRequest.getCount", member_email);
	}
	
	// 특정 타입의 데이터 개수
	public int getTypeCount(Map<String, Object> param) {
		return mybatis.selectOne("PtoRequest.getTypeCount", param);
	}
	
	// 특정 사람의 모든 타입 페이지에 따른 리스트
	public List<Pto_requestDTO> selectFromTo(Map<String, Object> param){
		return mybatis.selectList("PtoRequest.selectfromto", param);
	}
	
	// 특정 사람의 특정 타입 페이지에 따른 리스트
	public List<Pto_requestDTO> selectTypeFromTo(Map<String, Object> param){
		return mybatis.selectList("PtoRequest.selectTypeFromTo", param);
	}
	
	// 특정사람+시퀀스 번호로 DTO 가져오기
	public Pto_requestDTO getDetailBySeq(Map<String, Object> param) {
		return mybatis.selectOne("PtoRequest.getDetailBySeq", param);
	}
	
	//특성사람+시퀀스 번호의 데이터 수정하기(업데이트)
	public int updateDetailBoard(Pto_requestDTO dto) {
		return mybatis.update("PtoRequest.updateDetailBoard", dto);
	}
	
	//특정사람+시퀀스 번호 받아서 삭제하기(삭제)
	public int deleteDetailBoard(Map<String, Object> param) {
		int result= mybatis.delete("PtoRequest.deleteDetailBoard", param);
		return result;
	}
	
	//업로드
	public int upload(Pto_requestDTO dto) {
		return mybatis.insert("PtoRequest.upload", dto);
	}
	
	
	
	
	
	
}
