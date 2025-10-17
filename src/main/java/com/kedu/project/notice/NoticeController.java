package com.kedu.project.notice;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 		공지사항 관련 기능 controller
 * */

@RequestMapping("/board")
@RestController
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	// 전체 목록 조회
	@GetMapping("/notices")
	public ResponseEntity<List<NoticeDTO>> getNoticeList() {
		List<NoticeDTO> noticeList = noticeService.getNoticeList(); // 실제 DB 조회
		return ResponseEntity.ok(noticeList);
	}

	// {id} 조회
	@GetMapping("/detail/{notice_seq}")
	public ResponseEntity<NoticeDTO> getNoticeDetail(@PathVariable int notice_seq) {
		NoticeDTO notice = noticeService.getNoticeById(notice_seq);
		if (notice != null) {
			return ResponseEntity.ok(notice);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 공지사항 등록
	@PostMapping("/insert")
	public ResponseEntity<Integer> insertNotice(@RequestBody NoticeDTO dto) {
		int noticeInsert = noticeService.BoardInsert(dto);
		return ResponseEntity.ok(noticeInsert);
	}

	// 공지사항 수정
	@PutMapping("/update")
	public int updateNotice(@RequestBody NoticeDTO dto) {
		return noticeService.BoardUpdate(dto);
	}

	// 공지사항 삭제
	@DeleteMapping("/delete/{notice_seq}")
	public int deleteNotice(@PathVariable int notice_seq) {
		return noticeService.BoardDelete(notice_seq);
	}
}
