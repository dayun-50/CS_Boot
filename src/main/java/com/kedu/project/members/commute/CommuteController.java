package com.kedu.project.members.commute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.approval.ApprovalService;

/*
 *  	근태관리 기능 구현 Controller
 * */

@RequestMapping("")
@RestController
public class CommuteController {
	@Autowired
	private CommuteService CommuteService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
