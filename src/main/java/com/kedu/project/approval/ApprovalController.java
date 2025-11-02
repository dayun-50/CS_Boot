package com.kedu.project.approval;


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
import org.springframework.web.multipart.MultipartFile;

import com.kedu.project.config.PageNaviConfig;
import com.kedu.project.file.FileConstants;
import com.kedu.project.file.FileService;

import jakarta.servlet.http.HttpServletRequest;


/*
     전자결제 기능 구현 controller
 */
@RestController
@RequestMapping("/approval")

public class ApprovalController {


   @Autowired
   private ApprovalService approvalService;
   @Autowired //파일 트랜잭셔널 처리용 레이어
   private ApprovalFacadeService approvalFService;
 

    
    //특정 아이디에 대하여, 타입과 페이지에 따라 해당하는 값만 꺼내오기
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPagedApprovals(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {

        String member_email = "tlszn10@naver.com"; // 이후 토큰에서 꺼내도록 변경 예정

        int pageSize = PageNaviConfig.RECORD_COUNT_PER_PAGE;
        int cpage = page;
        int start = cpage * pageSize - (pageSize - 1);
        int end = cpage * pageSize;

        // 총 데이터 개수 조회
        int totalCount = (type == null || type.isBlank())
                ? approvalService.getCount(member_email)
                : approvalService.getTypeCount(member_email, type);

        if (totalCount == 0) {
            return ResponseEntity.noContent().build();
        }
        
        //토탈 페이지 조회
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // 현재 페이지에 해당하는 데이터 조회
        List<ApprovalDTO> pagedList = (type == null || type.isBlank())
                ? approvalService.selectFromTo(member_email, start, end)
                : approvalService.selectTypeFromTo(member_email, type, start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("list", pagedList);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }


   
   //디테일 전자결재 디테일 가져오기
   @GetMapping("/{seq}")
   public ResponseEntity<Map<String, Object>> getDetailBoard(@PathVariable int seq){
	   String member_email = "tlszn10@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	   
	   Map<String, Object> result =approvalFService.getDetailBySeq(seq,member_email);// 파사드 레이어에서 처리
	   if (result == null) {//204 No Content
	        return ResponseEntity.noContent().build(); //해당 전자결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok(result);
   }
   
   
   //디테일 전자결재 수정하기
   @PutMapping("/{seq}")
   public ResponseEntity<Void> updateDetailBoard(
           @PathVariable int seq,
           @RequestParam("approval_seq") String approval_seq,
           @RequestParam("approval_title") String approval_title,
           @RequestParam("approval_content") String approval_content,
           @RequestParam(required = false) MultipartFile[] files,
           @RequestParam(required = false) List<String> keepFiles) {

       String member_email = "tlszn10@naver.com"; // TODO: 토큰 추출 예정

       // DTO 세팅
       ApprovalDTO dto = new ApprovalDTO();
       dto.setApproval_seq(seq);
       dto.setApproval_title(approval_title);
       dto.setApproval_content(approval_content);
       dto.setMember_email(member_email);

       approvalFService.updateApprovalWithFiles(dto, files, keepFiles);// 파사드 레이어에서 처리
       return ResponseEntity.ok().build();
   }
   
   
   //디테일 전자결재 삭제하기
   @DeleteMapping("/{seq}")
   public ResponseEntity<Void> deleteDetailBoard(@PathVariable int seq) {
	    String member_email = "tlszn10@naver.com"; // 나중에 토큰에서 추출 예정
	    approvalFService.deleteApprovalWithFiles(seq, member_email); // 파사드 레이어에서 처리
	    return ResponseEntity.ok().build();
	}
   
   //전자결재 업로드 하기
   @PostMapping
   public ResponseEntity<Void> upload(ApprovalDTO dto, @RequestParam(required = false) MultipartFile[] files){
	   String member_email = "tlszn10@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	    dto.setMember_email(member_email);
	    approvalFService.upload(dto,files); // 파사드 레이어에서 처리
	    
	    return ResponseEntity.ok().build();
   }
   
   
}
