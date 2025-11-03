package com.kedu.project.chatting.chat_message;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kedu.project.file.FileConstants;
import com.kedu.project.file.FileDAO;
import com.kedu.project.file.FileDTO;
import com.kedu.project.file.FileService;


@Service
public class ChatFacadeService {

    @Autowired
    private Chat_messageService chat_messageService;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileDAO fileDao;
    
	
    @Transactional
    public Map<String, Object>  fileInsert(int chat_seq, String member_email, MultipartFile[] files) {
        try {
            // 1. 오리파일 이름 추출
            String oriName = files[0].getOriginalFilename();
            // 2.메시지 테이블에 저장 (파일 이름 그대로 메시지에 기록)
            Chat_messageDTO dto =chat_messageService.fileInsert(new Chat_messageDTO(0,chat_seq, member_email,oriName, null));
            
            // 3.GCS 업로드 (같은 메세지 시퀀스로 연결)
            int targetSeq =fileService.uploadChatFile(files, chat_seq, dto.getMessage_seq(),FileConstants.FC, member_email);
            FileDTO fdto = fileDao.findBySeq(targetSeq); // 시퀀스 번호로 DTO 조회

            
            Map<String, Object> result = new HashMap<>();
            result.put("dto", dto);
            result.put("fdto", fdto);

            
            return result;//전부 성공하면 파일을 메세지로 넣은 dto를 돌려보내기
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ 트랜잭션 실패", e);
        }
    }

}
