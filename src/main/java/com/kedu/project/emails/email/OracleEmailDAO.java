package com.kedu.project.emails.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/*
 *  이메일 기능 구현 DAO
 * */

@Repository
public class OracleEmailDAO {
	@Autowired @Qualifier("oracleSqlSessionTemplate")
	private SqlSession mybatis;
	
	
    // =======================================================
    //  메일 데이터 INSERT 
    // =======================================================
    /**
     * 'email' 테이블에 메일 정보를 저장하고, 자동 생성된 email_seq를 DTO에 채웁니다.
     */
    public int insertEmail(OracleEmailDTO dto) {
        // Mapper ID: Email.insertEmail (useGeneratedKeys 사용)
        return mybatis.insert("oracle.insertEmail", dto);
    }

    // =======================================================
    // 수신자 INSERT 
    // =======================================================
    /**
     * 'email_sender' 테이블에 수신자 정보를 저장합니다. (DTO 없이 인자로 처리)
     */
    public int insertEmailSender(int email_seq, String recipient) { 
        Map<String, Object> params = new HashMap<>();
        params.put("email_seq", email_seq);
        params.put("sender_email", recipient);
        
        // Mapper ID: oracle.insertEmailSender
        return mybatis.insert("oracle.insertEmailSender", params);
    }
    
    
    
   
	
}
