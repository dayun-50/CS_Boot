package com.kedu.project.file;

import java.util.List;
import java.util.Map;

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
	
	
	//파일 업로드
	public int upload(FileDTO dto) {
		return mybatis.insert("File.upload",dto);
	}
	
	//파일 리스트 꺼내오기
	public List<FileDTO> selectByParentSeq(Map <String, Object> param){
		return mybatis.selectList("File.selectByParentSeq", param);
	}
	
	// 종류+부모 시퀀스로 삭제하기 : 글 하나 지우면 거기에 딸린 모든 파일 삭제
	public int deleteByParentSeq(Map<String, Object> param) {
		return mybatis.delete("File.deleteByParentSeq", param);
	}
	
	//시스네임으로 개별 삭제
    public int deleteBySysname(String sysname) {
        return mybatis.delete("File.deleteBySysname", sysname);
    }
    
    //시스네임으로 오리네임 가져오기
    public String findOriNameBySysName(String sysname) {
    	return mybatis.selectOne("File.findOriNameBySysName", sysname);
    }
	
}
