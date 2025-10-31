package com.kedu.project.emails.email;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 *  이메일 기능 구현 DAO
 * */

@Repository
public class EmailDAO {
	@Autowired
	private SqlSession mybatis;
	
	// =======================================================
    // 1. 메일함 PK 조회 
    // =======================================================
    public Integer getEmailboxSeq(String memberEmail, String emailboxType) {
        Map<String, String> params = new HashMap<>();
        params.put("memberEmail", memberEmail);
        params.put("emailboxType", emailboxType);
        
        // Mapper ID: Email.getEmailboxSeq
        return mybatis.selectOne("Email.getEmailboxSeq", params);
    }
    
    // =======================================================
    // 2. 메일 데이터 INSERT 
    // =======================================================
    /**
     * 'email' 테이블에 메일 정보를 저장하고, 자동 생성된 email_seq를 DTO에 채웁니다.
     */
    public int insertEmail(EmailDTO dto) {
        // Mapper ID: Email.insertEmail (useGeneratedKeys 사용)
        return mybatis.insert("Email.insertEmail", dto);
    }

    // =======================================================
    // 3. 수신자 INSERT 
    // =======================================================
    /**
     * 'email_sender' 테이블에 수신자 정보를 저장합니다. (DTO 없이 인자로 처리)
     */
    public int insertEmailSender(int emailSeq, String senderEmail) { 
        Map<String, Object> params = new HashMap<>();
        params.put("emailSeq", emailSeq);
        params.put("senderEmail", senderEmail);
        
        // Mapper ID: Email.insertEmailSender
        return mybatis.insert("Email.insertEmailSender", params);
    }

    // =======================================================
    // 4. 기본 메일함 PK 생성 (회원가입시)
    // =======================================================
    /**
     * 사용자당 기본 메일함(INBOX, Sent) 정보를 DB에 저장합니다.
     */
    public int insertInbox(String memberEmail) {

        return mybatis.insert("Email_box.insertInbox", memberEmail);
    }
    
    public int insertSent(String email) {
        return mybatis.insert("Email_box.insertSent", email);
    }
    
    
    public int countByEmailAndType(String email, String type) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("type", type);
        return mybatis.selectOne("Email_box.countByEmailAndType", params);
    }
	
}
