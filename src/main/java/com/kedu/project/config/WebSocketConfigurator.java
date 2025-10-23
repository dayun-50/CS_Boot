package com.kedu.project.config;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

public class WebSocketConfigurator extends Configurator{
	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		String token = request.getParameterMap().get("token").get(0);
		String chat_seq = request.getParameterMap().get("chat_seq").get(0);
		sec.getUserProperties().put("token", token);
		sec.getUserProperties().put("chat_seq", chat_seq);
		System.out.println("dk"+chat_seq);
	}
	
    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        // 스프링 컨텍스트에서 WebSocket 엔드포인트 빈 가져오기
        return SpringProvider.ctx.getBean(endpointClass);
    }
}
