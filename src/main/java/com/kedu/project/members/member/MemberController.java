package com.kedu.project.members.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.external.james.JamesAccountService;
import com.kedu.project.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


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
	public ResponseEntity<Void> signup(@RequestBody MemberDTO dto) {
		System.out.println(dto);
		memberService.signup(dto);
		return ResponseEntity.ok().build();
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody MemberDTO dto, HttpServletRequest request){
		//바로 james 서버에 사용할 평문 비밀번호 만들어두기
		String rawPassword = dto.getPw();
		int result = memberService.login(dto);
		if(result > 0) { // 로그인 성공시
			 // 세션에 ID 저장
	        HttpSession session = request.getSession();
	        session.setAttribute("id", dto.getEmail());
	        //웹 인증 토큰
			String generalToken = jwt.createToken(dto.getEmail());
			//james 서버 인증 토큰
			String jamesAccessToken = jwt.createJamesToken(
		             dto.getEmail(),    
		             rawPassword // DTO에서 평문 비밀번호를 사용하여 토큰 B 생성
		         );
			// 3.  [핵심 수정] 두 토큰을 특정 구분자("|||")로 결합하여 하나의 String으로 반환
	         String combinedToken = generalToken + "|||" + jamesAccessToken;
			
			return ResponseEntity.ok(combinedToken);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("실패");
		}
	}
	
	// 비밀번호찾기(초반 이메일인증)
	@PostMapping("/findpw")
	public ResponseEntity<String> findpw(@RequestBody MemberDTO dto) {
		int result = memberService.findpw(dto);
		if (result > 0) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("실패");
		}
	}

	// 비밀번호 변경
	@PostMapping("/gnewpw")
	public ResponseEntity<String> gnewpw(@RequestBody MemberDTO dto) {
		int result = memberService.gnewpw(dto);
		if (result > 0) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("실패");
		}
	}

	// 마이페이지 출력
	@PostMapping("/mypage")
	public ResponseEntity<List<MemberDTO>> mypage(@RequestBody MemberDTO dto) {
		List<MemberDTO> list = memberService.mypage(dto);
		return ResponseEntity.ok(list);
	}

	// 마이페이지 수정
	@PostMapping("/updateMypage")
	public ResponseEntity<String> updateMypage(@RequestBody MemberDTO dto) {
		int result = memberService.updateMypage(dto);
		if (result > 0) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("실패");
		}
	}
}
