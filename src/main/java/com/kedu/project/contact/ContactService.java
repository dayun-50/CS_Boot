package com.kedu.project.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ContactService {
	@Autowired
	private ContactDAO dao;
}
