package com.kedu.project.emails.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("")
@RestController
public class EmailController {
	@Autowired
	private EmailService EmailService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
