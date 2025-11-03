package com.kedu.project.notice;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * 		공지사항 관련 기능 controller
 * */

@RequestMapping("/board")
@RestController
public class NoticeController {
	@Autowired
	private NoticeService noticeService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소

	// 전체 목록 조회
	@GetMapping("/notices")
	public ResponseEntity<List<NoticeDTO>> getNoticeList(@RequestParam String email) {
		List<NoticeDTO> noticeList = noticeService.getNoticeList(email); // 실제 DB 조회
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
}
