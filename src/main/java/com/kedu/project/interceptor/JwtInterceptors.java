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

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI();
		String method = request.getMethod();	
		if(method.equals("OPTIONS")) { // prefilght 보안 패킷은 그냥 허용
			return true;
		}

		if(path.equals("/member") || path.equals("/member/login") || path.equals("/member/findpw")
				|| path.equals("/member/gnewpw")) { // members 와 같은 경우
			if(method.equals("POST")) {
				return true;
			}
		}

		String authHeader = request.getHeader("Authorization");
		if(authHeader == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Error");
			return false;	
		}
		String token = authHeader.substring(7);
		System.out.println(token);

		try {
			DecodedJWT djwt = jwt.verifyToken(token);
			request.setAttribute("email", djwt.getSubject());

			return true;
		}catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Error");
			return false;
		}
	}
}
