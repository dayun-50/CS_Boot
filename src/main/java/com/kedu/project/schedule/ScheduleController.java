package com.kedu.project.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
먼슬리 ( 일정 테이블 ) 기능 구현 controller
*/

@RequestMapping("")
@RestController
public class ScheduleController {
	@Autowired
	private ScheduleService ScheduleService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
