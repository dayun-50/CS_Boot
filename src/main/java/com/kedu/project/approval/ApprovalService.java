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
    

   //멤버 지정 전체 리스트 조회
    public List<ApprovalDTO> getAll(String member_email) {
        List<ApprovalDTO> result = dao.getAll(member_email);
        convertStatusForUI(result);
        return result;
    }

    //멤버 지정 + 특정 타입 리스트 조회
    public List<ApprovalDTO> getType(String member_email, String type) {
        String symbolType = typeMap.get(type);

        Map<String, Object> param = new HashMap<>();
        param.put("member_email", member_email);
        param.put("type", symbolType);

        List<ApprovalDTO> result = dao.getType(param);
        convertStatusForUI(result);
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
    	return dao.upload(dto);
    }
    
    
}
