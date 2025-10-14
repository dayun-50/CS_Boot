package com.kedu.project.members.commute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
 *  	근태관리 기능 구현 Service
 * */

@Service
public class CommuteService {
	@Autowired
	private CommuteDAO dao;
}
