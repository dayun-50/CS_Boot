package com.kedu.project.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
먼슬리 ( 일정 테이블 ) 기능 구현 service
*/

@Service
public class ScheduleService {
	@Autowired
	private ScheduleDAO dao;
}
