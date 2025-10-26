package com.kedu.project.approval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 전자결재 기능 구현 Service
 */
@Service
public class ApprovalService {

    @Autowired
    private ApprovalDAO dao;

    // DB 조회용 상태 매핑
    private final Map<String, String> typeMap = Map.of(
        "inprogress", "w",
        "denied", "n",
        "approved", "y"
    );
    // UI 반환용 상태 매핑
    private final Map<String, String> reverseTypeMap = Map.of(
        "w", "처리중",
        "n", "반려",
        "y", "완료"
    );
  //DB 상태코드(w/n/y)를 UI 상태코드(inprogress/denied/approved)로 변환
    private void convertStatusForUI(List<ApprovalDTO> list) {
        for (ApprovalDTO dto : list) {
            String dbStatus = dto.getApproval_status();
            if (dbStatus != null) {
                dto.setApproval_status(reverseTypeMap.getOrDefault(dbStatus, ""));
            }
        }
    } 
    
    
    //모든타입의 데이터 개수 뽑아오기
    public int getCount(String email) {
        return dao.getCount(email);
    }
    //특정 타입의 데이터 개수 뽑아오기
    public int getTypeCount(String email, String type) {
    	String symbolType = typeMap.get(type);
    	
    	Map<String, Object> param = new HashMap<>();
    	param.put("member_email", email);
    	param.put("approval_status", symbolType);
    	
    	return dao.getTypeCount(param);
    }
    
    
    
   //모든 타입에 대하여 멤버 지정 페이지에 따른 데이터 조회 
    public List<ApprovalDTO> selectFromTo(String member_email, int start, int end) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("member_email", member_email);
    	param.put("start", start);
    	param.put("end", end);
    	
        List<ApprovalDTO> result = dao.selectFromTo(param);
        convertStatusForUI(result);
        return result;
    }

    //특정 타입에 대하여 맴버 지정 페이지에 따른 데이터 조회
    public List<ApprovalDTO> selectTypeFromTo(String member_email, String type, int start, int end){
        String symbolType = typeMap.get(type);
        
    	Map<String, Object> param = new HashMap<>();
    	param.put("member_email", member_email);
        param.put("approval_status", symbolType);    	
    	param.put("start", start);
    	param.put("end", end);
    	
        List<ApprovalDTO> result = dao.selectTypeFromTo(param);
        convertStatusForUI(result);
        
        System.out.println("요청 type: " + type);
        System.out.println("매핑된 symbolType: " + symbolType);
        return result;    	
    }

    //디테일 보드
    public ApprovalDTO getDetailBySeq(String member_email, int seq) {
    	Map<String,Object> param = new HashMap<>();
    	param.put("member_email", member_email);
    	param.put("seq", seq);
    	ApprovalDTO result = dao.getDetailBySeq(param);
    	
    	String dbStatus =result.getApproval_status();
    	result.setApproval_status(reverseTypeMap.getOrDefault(dbStatus, ""));
    	
    	return result;
    }
    
    //디테일 보드 수정 (업데이트)
    public int updateDetailBoard(ApprovalDTO dto) {
    	return dao.updateDetailBoard(dto);
    }
    
    //디테일 보드 삭제
    public int deleteDetailBoard(int approval_seq, String member_email) {
    	Map<String, Object> param= new HashMap<>();
    	param.put("approval_seq", approval_seq);
    	param.put("member_email", member_email);
    	return dao.deleteDetailBoard(param);
    }
    
    //보드 작성
    public int upload(ApprovalDTO dto) {
        dao.upload(dto);
        return dto.getApproval_seq(); 
    }
    
    
}
