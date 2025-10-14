package com.kedu.project.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
 *  연락처 기능 구현 Controller
 */

@RequestMapping("")
@RestController
public class ContactController {
	@Autowired
	private ContactService ContactService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
}
