package com.kedu.project.file;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
  	파일 업로드 및 다운로드 DAO 
  */
@Repository
public class FileDAO {
	@Autowired
	private SqlSession mybatis;
}
