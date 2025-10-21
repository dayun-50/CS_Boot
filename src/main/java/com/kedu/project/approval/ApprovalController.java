package com.kedu.project.approval;

import java.util.List;
import java.util.UUID;

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

import com.kedu.project.interceptor.JwtInterceptors;


/*
     전자결제 기능 구현 controller
 */
@RestController
@RequestMapping("/approval")

public class ApprovalController {

    private final JwtInterceptors jwtInterceptors;
   @Autowired
   private ApprovalService approvalService;


    ApprovalController(JwtInterceptors jwtInterceptors) {
        this.jwtInterceptors = jwtInterceptors;
    }

   
	
   //멤버지정해서 전체 리스트 가져오기
   @GetMapping()
    public ResponseEntity<List<ApprovalDTO>> test() {
	   String member_email = "cs@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	   List<ApprovalDTO> temp =approvalService.getAll(member_email);
	   
	   if (temp == null || temp.isEmpty()) {// 데이터가 없으면 204 No Content
	        return ResponseEntity.noContent().build();
	    }
	   return ResponseEntity.ok(temp);
    }
   
   
   //멤버지정 + 특정 처리 과정 리스트 전체 가져오기
   @GetMapping(params = "type")
   public ResponseEntity<List<ApprovalDTO>> getFilteredList(@RequestParam String type) {
	   String member_email = "cs@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	   List<ApprovalDTO> temp =approvalService.getType(member_email, type);

	   if (temp == null || temp.isEmpty()) {// 데이터가 없으면 204 No Content
	        return ResponseEntity.noContent().build();
	    }
	   return ResponseEntity.ok(temp);
   }
   
   
   //디테일 전자결재 디테일 가져오기
   @GetMapping("/{seq}")
   public ResponseEntity<ApprovalDTO> getDetailBoard(@PathVariable int seq){
	   String member_email = "cs@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	   ApprovalDTO temp =approvalService.getDetailBySeq(member_email,seq);

	   if (temp == null) {//204 No Content
	        return ResponseEntity.noContent().build(); //해당 전자결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok(temp);
   }
   
   
   //디테일 전자결재 수정하기
   @PutMapping("/{seq}")
   public ResponseEntity<Void> updateDetailBoard(@PathVariable int seq, @RequestBody ApprovalDTO dto){
	   String member_email = "cs@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	   dto.setMember_email(member_email);
	   int result =approvalService.updateDetailBoard(dto);
	   
	    if (result == 0) {
	    	return ResponseEntity.noContent().build(); //해당 전자결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok().build();
   }
   
   
   //디테일 전자결재 삭제하기
   @DeleteMapping("/{seq}")
   public ResponseEntity<Void> deleteDetailBoard(@PathVariable int seq){
	   String member_email = "cs@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	   int result =approvalService.deleteDetailBoard(seq, member_email);
	   
	    if (result == 0) {
	    	return ResponseEntity.noContent().build(); //해당 전자결재에 권한없는 사람이 접근한것
	    }
	   return ResponseEntity.ok().build();
   }
   
   //전자결재 업로드 하기
   @PostMapping
   public ResponseEntity<Void> upload(ApprovalDTO dto, @RequestParam(required = false) MultipartFile[] files){
	   
	   
	   if(files != null) { //파일이 존재한다면
	   //파일들어오는지 확인용 : 이후에 완성시켜야 함
	    for(MultipartFile file : files) {
	        if(!file.isEmpty()) {
	            String sysname = UUID.randomUUID() + "_" + file.getOriginalFilename();
	            System.out.println(sysname);
	            /*BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, sysname))
	                                        .setContentType(file.getContentType())
	                                        .build();
	            try (InputStream is = file.getInputStream()) {
	                storage.create(blobInfo, is.readAllBytes());
	            } catch (Exception e) {
	                e.printStackTrace();
	                return ResponseEntity.status(500).build();
	            }*/
	        }
	    }
	   }
	    String member_email = "cs@naver.com";// 토큰으로 변경되면 토큰에서 꺼낸 작성자로 가져와야함
	    dto.setMember_email(member_email);
	    System.out.println(dto.getMember_email());
	    System.out.println(dto.getApproval_title());
	    System.out.println(dto.getApproval_content());
	    approvalService.upload(dto);
	    
	    
	    
	    return ResponseEntity.ok().build();
	   
   }
   
   
}
