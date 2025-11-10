package com.kedu.project.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
	@Bean
	public ServerEndpointExporter serverEndpointExporter(ApplicationContext ctx) {
		System.out.println("여까지왓소");
		SpringProvider.ctx = ctx;
		return new ServerEndpointExporter();
	}
}
