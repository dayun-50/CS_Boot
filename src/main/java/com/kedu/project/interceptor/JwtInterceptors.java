package com.kedu.project.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kedu.project.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptors implements HandlerInterceptor{
	@Autowired
	private static JwtUtil jwt;
//	
////	@Override
//	public static String getEmailFromToken(String token)
//			throws Exception {
////	
//	}
}
