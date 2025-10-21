package com.kedu.project.emails.email_sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class Email_senderService {
	@Autowired
	private Email_senderDAO dao;
}
