package com.kedu.project.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
	@Value("")
	private Long exp;
	
//	private Algorithm algorithm; 
//	private JWTVerifier jwt;
}
