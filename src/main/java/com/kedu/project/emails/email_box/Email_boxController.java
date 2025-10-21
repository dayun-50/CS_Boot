package com.kedu.project.emails.email_box;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *  	이메일함 기능 관련 controller
 * */

@RequestMapping("")
@RestController
public class Email_boxController {
	@Autowired
	private Email_boxService Email_boxService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
