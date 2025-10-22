package com.kedu.project.approval;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
  	전자결제 기능 구현 DAO
 */
@Repository
public class ApprovalDAO {
	@Autowired
	private SqlSession mybatis;
	
	// 전체 타입의 데이터 개수
	public int getCount(String member_email) {
		return mybatis.selectOne("Approval.getCount", member_email);
	}
	
	// 특정 타입의 데이터 개수
	public int getTypeCount(Map<String, Object> param) {
		return mybatis.selectOne("Approval.getTypeCount", param);
	}
	
	// 특정 사람의 모든 타입 페이지에 따른 리스트
	public List<ApprovalDTO> selectFromTo(Map<String, Object> param){
		return mybatis.selectList("Approval.selectfromto", param);
	}
	
	// 특정 사람의 특정 타입 페이지에 따른 리스트
	public List<ApprovalDTO> selectTypeFromTo(Map<String, Object> param){
		return mybatis.selectList("Approval.selectTypeFromTo", param);
	}
	
	// 특정사람+시퀀스 번호로 DTO 가져오기
	public ApprovalDTO getDetailBySeq(Map<String, Object> param) {
		return mybatis.selectOne("Approval.getDetailBySeq", param);
	}
	
	//특성사람+시퀀스 번호의 데이터 수정하기(업데이트)
	public int updateDetailBoard(ApprovalDTO dto) {
		return mybatis.update("Approval.updateDetailBoard", dto);
	}
	
	//특정사람+시퀀스 번호 받아서 삭제하기(삭제)
	public int deleteDetailBoard(Map<String, Object> param) {
		return mybatis.delete("Approval.deleteDetailBoard", param);
	}
	
	//업로드
	public int upload(ApprovalDTO dto) {
		return mybatis.insert("Approval.upload", dto);
	}
	
}
