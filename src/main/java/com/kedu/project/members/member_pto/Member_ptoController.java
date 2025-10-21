package com.kedu.project.members.member_pto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 		남은 연차 관리 controller
 * */

@RequestMapping("")
@RestController
public class Member_ptoController {
	@Autowired
	private Member_ptoService Member_ptoService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
