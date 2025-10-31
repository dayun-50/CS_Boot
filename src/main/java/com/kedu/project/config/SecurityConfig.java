package com.kedu.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		System.out.println("아");
		http
		.csrf(csrf -> csrf.disable())   // CSRF 비활성화
		// 💡 [핵심 해결책 1] Basic Authentication 완전 비활성화 (브라우저 로그인 창 방지)
        .httpBasic(AbstractHttpConfigurer::disable) 
        
        // 💡 [핵심 해결책 2] 세션 기반 인증 비활성화 (REST API 표준)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/**").permitAll() 
				.anyRequest().authenticated()
				)
		.httpBasic(httpBasic -> {}); // 최신 방식, 빈 람다로 대체
		return http.build();
	}
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/chatting/**");
    }
}
