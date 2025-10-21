package com.kedu.project.members.member_pto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 		남은 연차 관리 service
 * */

import com.kedu.project.approval.ApprovalDAO;

@Service
public class Member_ptoService {
	@Autowired
	private Member_ptoDAO dao;
}
