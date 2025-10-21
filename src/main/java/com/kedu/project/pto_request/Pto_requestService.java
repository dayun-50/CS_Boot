package com.kedu.project.pto_request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.approval.ApprovalDAO;


/*
 * 		연차 신청 기능 관련 service
 * */

@Service
public class Pto_requestService {
	@Autowired
	private ApprovalDAO dao;
}
