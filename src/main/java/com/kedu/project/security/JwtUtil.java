package com.kedu.project.security;

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

	// 토큰설정
	public String createToken(String id) {
		return JWT.create().withSubject(id) // 이메일을 대표로 넣어둠
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + exp)).sign(this.algorithm);
	}

	public DecodedJWT verifyToken(String token) {
		return jwt.verify(token);
	}
}
