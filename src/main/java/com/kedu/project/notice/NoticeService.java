package com.kedu.project.notice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 		공지사항 관련 기능 service
 * */

@Service
public class NoticeService {
	@Autowired
	private NoticeDAO dao;
	
	public List<NoticeDTO> getNoticeList(){
		return dao.getNoticeList();
	}
	
	public NoticeDTO getNoticeById(int notice_seq) {
		return dao.getNoticeById(notice_seq);
	}
	
	public int BoardInsert(NoticeDTO dto) {
	    return dao.BoardInsert(dto);
	}
	
	public int BoardUpdate(NoticeDTO dto) {
		return dao.BoardUpdate(dto);
	}
	
	public int BoardDelete(int notice_seq) {
		return dao.BoardDelete(notice_seq);
	}
}
