package com.kedu.project.pto_request;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;


/*
 * 		연차 신청 기능 관련 controller
 * */

@RequestMapping("/ptorequest")
@RestController
public class Pto_requestController {
	
	@Autowired
	private Pto_requestService pto_requestService;
	
    //특정 아이디에 대하여 페이지에 따라 해당하는 값만 꺼내오기 : 연차
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPagedPtoRequest(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {

        String member_email = "test01@test.com"; // 이후 토큰에서 꺼내도록 변경 예정

        int pageSize = 5;
        int cpage = page;
        int start = cpage * pageSize - (pageSize - 1);
        int end = cpage * pageSize;

        // 총 데이터 개수 조회
        int totalCount = (type == null || type.isBlank())
                ? pto_requestService.getCount(member_email)
                : pto_requestService.getTypeCount(member_email, type);

        if (totalCount == 0) {
            return ResponseEntity.noContent().build();
        }
        
        //토탈 페이지 조회
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // 현재 페이지에 해당하는 데이터 조회
        List<Pto_requestDTO> pagedList = (type == null || type.isBlank())
                ? pto_requestService.selectFromTo(member_email, start, end)
                : pto_requestService.selectTypeFromTo(member_email, type, start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("list", pagedList);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }


   
   //연차 디테일 가져오기 
   @GetMapping("/{seq}")
   public ResponseEntity<Pto_requestDTO> getDetailBoard(@PathVariable int seq){
	   String member_email = "test01@test.com"; // 이후 토큰에서 꺼내도록 변경 예정
	   Pto_requestDTO temp =pto_requestService.getDetailBySeq(member_email,seq);

	   if (temp == null) {//204 No Content
	        return ResponseEntity.noContent().build(); //해당결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok(temp);
   }
   
   
   //디테일 연차 수정하기
   @PutMapping("/{seq}")
   public ResponseEntity<Void> updateDetailBoard(@PathVariable int seq, @RequestBody Pto_requestDTO dto){
	   String member_email = "test01@test.com"; // 이후 토큰에서 꺼내도록 변경 예정

	   dto.setMember_email(member_email);
	   int result =pto_requestService.updateDetailBoard(dto);
	    if (result == 0) {
	    	return ResponseEntity.noContent().build(); //해당결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok().build();
   }
   
   
   //디테일 연차 삭제하기
   @DeleteMapping("/{seq}")
   public ResponseEntity<Void> deleteDetailBoard(@PathVariable int seq){
	   String member_email = "test01@test.com"; // 이후 토큰에서 꺼내도록 변경 예정
	   int result =pto_requestService.deleteDetailBoard(seq, member_email);
	   
	    if (result == 0) {
	    	return ResponseEntity.noContent().build(); //해당결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok().build();
   }
   
   //연차 신청하기
   @PostMapping
   public ResponseEntity<Void> insertPtoRequest(@RequestBody Pto_requestDTO dto){
	   String member_email = "test01@test.com"; // 이후 토큰에서 꺼내도록 변경 예정
	   Timestamp pto_start_at = Timestamp.valueOf(dto.getPto_start_at().toLocalDateTime());
	   Timestamp pto_end_at = Timestamp.valueOf(dto.getPto_end_at().toLocalDateTime());

	   
	   dto.setMember_email(member_email);
	   dto.setPto_start_at(pto_start_at);
	   dto.setPto_end_at(pto_end_at);
	   
	   pto_requestService.upload(dto);
	   return ResponseEntity.ok().build();
   }
	
	

}
