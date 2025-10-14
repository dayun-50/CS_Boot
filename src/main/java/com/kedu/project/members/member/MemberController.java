package com.kedu.project.members.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.approval.ApprovalService;

/*
 * 		사원 회원가입 및 마이페이지 구현 Controller
 * */

@RequestMapping("")
@RestController
public class MemberController {
	@Autowired
	private MemberService MemberService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
