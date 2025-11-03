package com.kedu.project.emails.james;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class JamesEmailDAO {
    //  [핵심] MySQL DB 전용 통로만 주입
    @Autowired 
    private SqlSession mybatis; 
    
    private static final String NAMESPACE = "JamesMailbox"; // Mapper namespace와 일치

    // 1. 메일함 ID 조회 (MySQL 쿼리)
    public Integer getEmailboxSeq(String memberEmail, String emailboxType) {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", memberEmail);
        params.put("mailbox_name", emailboxType);
        
        // 쿼리 ID: JamesMailbox.getEmailboxSeq
        return mybatis.selectOne(NAMESPACE + ".getEmailboxSeq", params);
    }
}
