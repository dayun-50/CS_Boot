package com.kedu.project.emails.email_box;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 		이메일함 기능 관련 Service
 * */

@Service
public class Email_boxService {
	@Autowired
	private Email_boxDAO dao;
}
