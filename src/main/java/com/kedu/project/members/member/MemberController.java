package com.kedu.project.members.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.security.JwtUtil;


/*
 * 		사원 회원가입 및 마이페이지 구현 Controller
 * */

@RequestMapping("/member")
@RestController
public class MemberController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private JwtUtil jwt;

	// 회원가입
	@PostMapping
	public ResponseEntity<Void> signup(@RequestBody MemberDTO dto){
		System.out.println(dto);
		memberService.signup(dto);
		return ResponseEntity.ok().build();
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody MemberDTO dto){ 
		int result = memberService.login(dto);
		System.out.println(result);
		System.out.println(dto.getEmail());
		System.out.println(dto.getPw());
		if(result > 0) { // 로그인 성공시
			String token = jwt.createToken(dto.getEmail());
			return ResponseEntity.ok(token);
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("실패");
		}	
	}
}
