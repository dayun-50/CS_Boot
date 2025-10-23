package com.kedu.project.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
@Component
public class JwtUtil {
	@Value("${jwt.expiration}")
	private Long exp;

	private Algorithm algorithm; 
	private JWTVerifier jwt;

	// 생성자
	public JwtUtil(@Value("${jwt.secret}") String secret) { // 비번
		this.algorithm = Algorithm.HMAC256(secret);
		this.jwt = JWT.require(algorithm).build();
	}
	
	// 일반 웹 토큰 설정
	public String createToken(String id) {
		return JWT.create()
				.withSubject(id) // 이메일을 대표로 넣어둠
				.withIssuedAt(new Date(System.currentTimeMillis()))         
				.withExpiresAt(new Date(System.currentTimeMillis() + exp)) 
				.sign(this.algorithm);
	}
	
	//james 서버 전용 토큰
	public String createJamesToken(String email, String rawPassword) {
        // 평문 비밀번호를 Base64로 인코딩하여 저장 (보안 강화를 위해)
        String encodedPassword = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
        
        return JWT.create()
            .withSubject(email) 
            .withClaim("james_pw", encodedPassword) // Base64 인코딩된 비밀번호 저장
            .withIssuedAt(new Date(System.currentTimeMillis()))
            // 💡 James 접근 티켓은 일반 JWT보다 짧은 만료 시간(예: 10분)을 갖는 것이 보안상 권장되지만,
            // 여기서는 기존 exp 설정을 사용하겠습니다.
            .withExpiresAt(new Date(System.currentTimeMillis() + exp))
            .sign(this.algorithm);
    }
	public String getRawJamesPassword(String ticket) {
        DecodedJWT jwt = verifyToken(ticket); // 토큰 유효성 검증
        String encodedPassword = jwt.getClaim("james_pw").asString();
        
        // Base64 디코딩하여 평문 비밀번호 반환
        return new String(Base64.getDecoder().decode(encodedPassword), StandardCharsets.UTF_8);
    }
	
	
	public DecodedJWT verifyToken(String token) {
		return jwt.verify(token);
	}
}
