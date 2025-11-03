package com.kedu.project.notice;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		공지사항 관련 기능 DAO
 * */

@Repository
public class NoticeDAO {
	@Autowired
	private SqlSession mybatis;

	public List<NoticeDTO> getNoticeList(String company_code) {
		return mybatis.selectList("NoticeMapper.getNoticeList", company_code);
	}

	public NoticeDTO getNoticeById(int notice_seq) {
		return mybatis.selectOne("NoticeMapper.getNoticeById", notice_seq);
	}
	
	// 뷰 카운트 추가후 update
	public int updateViewCount(NoticeDTO dto, int viewCont) {
		return mybatis.update("NoticeMapper.updateViewCount",Map.of(
				"notice_seq", dto.getNotice_seq(),
				"view_count", viewCont));
	}

}
