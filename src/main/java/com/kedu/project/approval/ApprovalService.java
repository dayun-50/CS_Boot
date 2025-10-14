package com.kedu.project.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
  	전자결제 기능 구현 Service
 */
@Service
public class ApprovalService {
	@Autowired
	private ApprovalDAO dao;
}
