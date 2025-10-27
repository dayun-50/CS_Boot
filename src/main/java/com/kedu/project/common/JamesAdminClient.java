package com.kedu.project.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class JamesAdminClient {
//
//    private final WebClient webClient;
//    
//    // application.properties 또는 application.yml에서 설정 값 주입
//    // 예: james.admin.url=http://localhost:9000
//    // 예: james.admin.username=admin
//    // 예: james.admin.password=root
//
//    public JamesAdminClient(
//        @Value("${james.admin.url}") String jamesAdminUrl,
//        @Value("${james.admin.username}") String adminUsername,
//        @Value("${james.admin.password}") String adminPassword) {
//        
//        // WebClient 초기화: 기본 URL 및 인증 정보 설정
//        this.webClient = WebClient.builder()
//                .baseUrl(jamesAdminUrl)
//                .defaultHeaders(header -> header.setBasicAuth(adminUsername, adminPassword))
//                .build();
//    }
//
//    /**
//     * James 서버에 새로운 메일 계정을 생성합니다.
//     * @param email 생성할 이메일 주소 (member.email)
//     * @param rawPassword 계정 비밀번호 (암호화되지 않은 원본)
//     * @throws RuntimeException James 서버 통신 오류 또는 계정 생성 실패 시 발생
//     */
//    public void createMailAccount(String email, String rawPassword) {
//        
//        // REST API 요청 본문 (JSON): {"password": "비밀번호"}
//        String requestBody = String.format("{\"password\": \"%s\"}", rawPassword);
//
//        try {
//            this.webClient.put()
//                .uri("/users/{email}", email) // PUT /users/user@example.com 호출
//                .bodyValue(requestBody)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
//                          response -> response.createException()) 
//                .toBodilessEntity()
//                .block(); 
//
//        } catch (WebClientResponseException e) {
//            // 4xx/5xx 응답 에러 처리
//            throw new RuntimeException("James 서버 계정 생성 실패: " + e.getResponseBodyAsString(), e);
//        } catch (Exception e) {
//             // 기타 통신 오류 처리
//            throw new RuntimeException("James 서버와 통신 중 오류 발생", e);
//        }
//    }
}