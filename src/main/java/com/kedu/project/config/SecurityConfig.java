package com.kedu.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		System.out.println("아");
		http
		.csrf(csrf -> csrf.disable())   // CSRF 비활성화
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
