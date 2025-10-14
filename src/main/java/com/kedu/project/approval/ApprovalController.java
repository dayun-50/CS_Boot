package com.kedu.project.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
  	전자결제 기능 구현 controller
 */
@RequestMapping("")
@RestController
public class ApprovalController {
	@Autowired
	private ApprovalService ApprovalService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
