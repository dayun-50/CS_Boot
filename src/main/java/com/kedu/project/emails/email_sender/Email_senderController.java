package com.kedu.project.emails.email_sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
		수신자 기능 관련 Controller
*/
@RequestMapping("")
@RestController
public class Email_senderController {
	@Autowired
	private Email_senderService Email_senderService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
