package com.kedu.project.chatting.chat_message;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.file.FileConstants;
import com.kedu.project.file.FileDTO;
import com.kedu.project.file.FileService;


/*
	채팅방 메세지 기능 구현 controller
 */

@RequestMapping("/chatMessage")
@RestController
public class Chat_messageController {
	@Autowired
	private Chat_messageService chat_messageService;
	@Autowired
	private FileService fileService;

	//채팅방 번호로 리스트 가져오기
	@GetMapping("/{seq}")
	public ResponseEntity<List<FileDTO>> getDetailBoard(@PathVariable int seq){
		List<FileDTO> list =fileService.getFilesByChatSeq(seq, FileConstants.FC);
		return ResponseEntity.ok(list);
	}

	// 메세지 글자로 검색
	@PostMapping("/serchByText")
	public ResponseEntity<Map<String, Object>> serchByText(@RequestBody Chat_messageDTO dto){
		Map<String, Object> list = chat_messageService.serchByText(dto);
		return ResponseEntity.ok(list);
	}

	// 메세지 날짜로 검색
	@PostMapping("/serchByDate")
	public ResponseEntity<Map<String, Object>> serchByDate(@RequestBody Chat_messageDTO dto){
		Map<String, Object> list = chat_messageService.serchByDate(dto);
		return ResponseEntity.ok(list);
	}
	
	// 파일 제목 검색
	@PostMapping("/serchByFileText")
	public ResponseEntity<List<FileDTO>> serchByFileText(@RequestBody FileDTO dto){
		List<FileDTO> list = fileService.serchByFileText(dto, FileConstants.FC);
		return ResponseEntity.ok(list);
	}
}
