package com.kedu.project.pto_request;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * 		연차 신청 기능 관련 DAO
 * */

@Repository
public class Pto_requestDAO {
	@Autowired
	private SqlSession mybatis;
}
