package com.kedu.project.approval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kedu.project.file.FileConstants;
import com.kedu.project.file.FileDTO;
import com.kedu.project.file.FileService;


@Service
@Transactional
public class ApprovalFacadeService {

	@Autowired
	private ApprovalService approvalService;
	@Autowired
	private FileService fileService;
	
	// 0. GCS 업로드중 실패시 정리용 (부모시퀀스로 올라간 파일 전부 정리)
    private void cleanupUploadedFiles(ApprovalDTO dto) {
        try {
            fileService.deleteFilesByParent(dto.getApproval_seq(), FileConstants.FA);
        } catch (Exception ignore) {}
    }

	//1. 업로드
	public void upload(ApprovalDTO dto, MultipartFile[] files) {
		try {
			//1. 게시글 저장
			int parentseq= approvalService.upload(dto);
			//2. 파일 저장
			if(files != null) {// 파일이 존재한다면
				fileService.upload(files, parentseq,FileConstants.FA, dto.getMember_email());
			}
		}catch(Exception e) {//gcs업로드 중 실패시, 삭제후 롤백유도
			cleanupUploadedFiles(dto);
            throw e; // 롤백 유도	
		}
	}
	
	//2. 디테일글번호에 맞는 파일이름 리스트가져오기
    public Map<String, Object> getDetailBySeq(int seq, String member_email) {
        Map<String, Object> result = new HashMap<>();

        // 1. 결재 본문
        ApprovalDTO approval = approvalService.getDetailBySeq(member_email, seq);
        // 2. 파일 목록
        List<FileDTO> fileNames =  fileService.getFilesByParent(seq, FileConstants.FA);

        // 3. 합쳐서 리턴
        result.put("approval", approval);
        result.put("files", fileNames);
        return result;
    }
    
    //3. 글번호 따라서 삭제하기
    public int deleteApprovalWithFiles(int seq, String member_email) {
        // 1. Approval 삭제 (권한 검사 포함)
        int result = approvalService.deleteDetailBoard(seq, member_email);
        if (result == 0) {return 0;};// 내 글이 아니면 리턴

        // 2. 관련 파일 GCS 및 DB에서 삭제
        return fileService.deleteFilesByParent(seq, FileConstants.FA);
    }
    
    //4. 업데이트
    public void updateApprovalWithFiles(ApprovalDTO dto, MultipartFile[] files, List<String> keepFiles) {
    	int result = approvalService.updateDetailBoard(dto);
    	if (result == 0) {throw new RuntimeException("권한이 없거나 수정 실패");}

    	// 기존 파일 중 keepFiles 제외하고 삭제
    	fileService.deleteFilesExcept(dto.getApproval_seq(), FileConstants.FA, keepFiles);

    	// 새 파일 업로드
    	if (files != null && files.length > 0) {
    		fileService.upload(files, dto.getApproval_seq(), FileConstants.FA, dto.getMember_email());
    	}
    }
	
}
