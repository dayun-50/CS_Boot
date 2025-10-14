package com.kedu.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kedu.project.interceptor.JwtInterceptors;
	

public class InterceptorConfig implements WebMvcConfigurer{
	@Autowired
	private JwtInterceptors jwtInterceptor;
	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(jwtInterceptor)
//		.addPathPatterns("/**")
//		.excludePathPatterns("/auth", "/users");
//	}
}
