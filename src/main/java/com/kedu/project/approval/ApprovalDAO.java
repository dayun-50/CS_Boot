package com.kedu.project.approval;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
  	전자결제 기능 구현 DAO
 */
@Repository
public class ApprovalDAO {
	@Autowired
	private SqlSession mybatis;
}
