package com.kedu.project.notice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.members.member.MemberDAO;
import com.kedu.project.members.member.MemberDTO;

/*
 * 		공지사항 관련 기능 service
 * */

@Service
public class NoticeService {
	@Autowired
	private NoticeDAO dao;
	
	@Autowired
	private MemberDAO MemberDAO;

	public List<NoticeDTO> getNoticeList(String email) {
		String companyCode = MemberDAO.selectMemberCompany(email);
		return dao.getNoticeList(companyCode);
	}

	public NoticeDTO getNoticeById(int notice_seq) {
		NoticeDTO notice = dao.getNoticeById(notice_seq);
		notice.setView_count(notice.getView_count()+1);
		int viewCont = notice.getView_count();
		dao.updateViewCount(notice, viewCont);
		return notice;
	}

}
