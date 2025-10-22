package com.kedu.project.pto_request;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/*
 * 		연차 신청 기능 관련 service
 * */

@Service
public class Pto_requestService {
	@Autowired
	private Pto_requestDAO dao;
	
    // DB 조회용 상태 매핑
    private final Map<String, String> typeMap = Map.of(
        "inprogress", "w",
        "denied", "n",
        "approved", "y"
    );
    // UI 반환용 상태 매핑
    private final Map<String, String> reverseTypeMap = Map.of(
        "w", "대기",
        "n", "반려",
        "y", "완료"
    );
  //DB 상태코드(w/n/y)를 UI 상태코드(inprogress/denied/approved)로 변환
    private void convertStatusForUI(List<Pto_requestDTO> list) {
        for (Pto_requestDTO dto : list) {
            String dbStatus = dto.getPto_status();
            if (dbStatus != null) {
                dto.setPto_status(reverseTypeMap.getOrDefault(dbStatus, ""));
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
    	param.put("pto_status", symbolType);
    	
    	return dao.getTypeCount(param);
    }
    
    
    
   //모든 타입에 대하여 멤버 지정 페이지에 따른 데이터 조회 
    public List<Pto_requestDTO> selectFromTo(String member_email, int start, int end) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("member_email", member_email);
    	param.put("start", start);
    	param.put("end", end);
    	
        List<Pto_requestDTO> result = dao.selectFromTo(param);
        convertStatusForUI(result);
        return result;
    }

    //특정 타입에 대하여 맴버 지정 페이지에 따른 데이터 조회
    public List<Pto_requestDTO> selectTypeFromTo(String member_email, String type, int start, int end){
        String symbolType = typeMap.get(type);
        
    	Map<String, Object> param = new HashMap<>();
    	param.put("member_email", member_email);
        param.put("pto_status", symbolType);    	
    	param.put("start", start);
    	param.put("end", end);
    	
        List<Pto_requestDTO> result = dao.selectTypeFromTo(param);
        convertStatusForUI(result);
        
        System.out.println("요청 type: " + type);
        System.out.println("매핑된 symbolType: " + symbolType);
        return result;    	
    }

    //디테일 보드
    public Pto_requestDTO getDetailBySeq(String member_email, int seq) {
    	Map<String,Object> param = new HashMap<>();
    	param.put("member_email", member_email);
    	param.put("seq", seq);
    	Pto_requestDTO result = dao.getDetailBySeq(param);
    	
    	String dbStatus =result.getPto_status();
    	result.setPto_status(reverseTypeMap.getOrDefault(dbStatus, ""));
    	
    	return result;
    }
    
    //디테일 보드 수정 (업데이트)
    public int updateDetailBoard(Pto_requestDTO dto) {
    	return dao.updateDetailBoard(dto);
    }
    
    //디테일 보드 삭제
    public int deleteDetailBoard(int pto_seq, String member_email) {
    	Map<String, Object> param= new HashMap<>();
    	param.put("pto_seq", pto_seq);
    	param.put("member_email", member_email);
    	return dao.deleteDetailBoard(param);
    }
    
    //보드 작성
    public int upload(Pto_requestDTO dto) {
    	int pto_used= this.calculatePto_used(dto.getPto_start_at(), dto.getPto_end_at());
    	dto.setPto_used(pto_used);
    	return dao.upload(dto);
    }
    
    public int calculatePto_used(Timestamp pto_start_at, Timestamp pto_end_at) {
        LocalDateTime start = pto_start_at.toLocalDateTime();
        LocalDateTime end = pto_end_at.toLocalDateTime();

        boolean isSameDay = start.toLocalDate().equals(end.toLocalDate());

        if (isSameDay) {
            if (start.toLocalTime().equals(LocalTime.of(9, 0)) && end.toLocalTime().equals(LocalTime.of(18, 0))) {
                return 8;
            } else if (start.toLocalTime().equals(LocalTime.of(9, 0)) && end.toLocalTime().equals(LocalTime.of(14, 0))) {
                return 4;
            } else if (start.toLocalTime().equals(LocalTime.of(14, 0)) && end.toLocalTime().equals(LocalTime.of(18, 0))) {
                return 4;
            } else {
                return 0;
            }
        }

        int pto_used = 0;
        long daysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()) + 1;

        // 첫째날
        if (start.toLocalTime().equals(LocalTime.of(9, 0))) {
            pto_used += 8;
        } else if (start.toLocalTime().equals(LocalTime.of(14, 0))) {
            pto_used += 4;
        }

        // 중간날
        if (daysBetween > 2) {
            pto_used += (daysBetween - 2) * 8;
        }

        // 마지막날
        if (end.toLocalTime().equals(LocalTime.of(18, 0))) {
            pto_used += 8;
        } else if (end.toLocalTime().equals(LocalTime.of(14, 0))) {
            pto_used += 4;
        }

        return pto_used;
    }

    

	
}
