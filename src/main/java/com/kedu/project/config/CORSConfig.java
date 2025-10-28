package com.kedu.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * 여기일단 초기설정으로 가져왔구요 요것저것 만들고 나중에 ip맞추는걸로~
 */
@Configuration
public class CORSConfig implements WebMvcConfigurer{
	 @Override
	   public void addCorsMappings(CorsRegistry registry) {
	      registry.addMapping("/**")


	            .allowedOrigins("http://10.5.5.5:3000") // 모든 출처 -> 나중에 서버맞춰지면 설정
	            .allowedMethods("*") 
	            .allowedHeaders("*")
	            .allowCredentials(true); 
	   }
}
