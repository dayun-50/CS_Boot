package com.kedu.project.schedule;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
	먼슬리 ( 일정 테이블 ) 기능 구현 DAO
*/
@Repository
public class ScheduleDAO {
	@Autowired
	private SqlSession mybatis;
}
