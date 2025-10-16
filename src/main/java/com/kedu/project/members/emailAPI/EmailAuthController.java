package com.kedu.project.members.emailAPI;
/*
 * 	회원가입 및 마이페이지 이메일 인증 API 전용 controller
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.members.member.MemberDTO;

@RestController
@RequestMapping("/emailauth")
public class EmailAuthController {
	@Autowired
	private EamilAuthService emailService;
	
	@PostMapping
	public ResponseEntity<String> emailAuth(@RequestBody MemberDTO dto)throws Exception{
		String result = emailService.doPost(dto.getEmail());
		if(result != null) {
			return ResponseEntity.ok(result);
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 발송 실패");
		}
	}
	
	@ExceptionHandler
    public ResponseEntity<String> handleMessagingException(Exception e) {
        e.printStackTrace();
        System.out.println("메일관련 오류");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("이메일 발송 중 오류가 발생했습니다.");
    }
}
