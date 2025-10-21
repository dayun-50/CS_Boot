package com.kedu.project.pto_request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
 * 		연차 신청 기능 관련 controller
 * */

@RequestMapping("")
@RestController
public class Pto_requestController {
	@Autowired
	private Pto_requestService Pto_requestService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
