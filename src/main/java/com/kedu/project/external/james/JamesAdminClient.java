package com.kedu.project.external.james;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class JamesAdminClient {

	private final WebClient webClient;

	// application.properties 또는 application.yml에서 설정 값 주입
	// 예: james.admin.url=http://localhost:9000
	// 예: james.admin.username=admin
	// 예: james.admin.password=root

	public JamesAdminClient(@Value("${james.admin.url}") String jamesAdminUrl,
			@Value("${james.admin.username}") String adminUsername,
			@Value("${james.admin.password}") String adminPassword) {

		// WebClient 초기화: 기본 URL 및 인증 정보 설정
		this.webClient = WebClient.builder().baseUrl(jamesAdminUrl)
				.defaultHeaders(header -> header.setBasicAuth(adminUsername, adminPassword)).build();
	}

	/**
	 * James 서버에 새로운 메일 계정을 생성합니다.
	 * 
	 * @param email       생성할 이메일 주소 (id + 회사도메인)
	 * @param rawPassword 계정 비밀번호 (암호화되지 않은 원본)
	 * @throws RuntimeException James 서버 통신 오류 또는 계정 생성 실패 시 발생
	 */
	public void createMailAccount(String email, String rawPassword) {

		// REST API 요청 본문 (JSON): {"password": "비밀번호"}
		String requestBody = String.format("{\"password\": \"%s\"}", rawPassword);

		try {
			this.webClient.put().uri("/users/{email}", email) // PUT /users/'id@domain.com' 호출
					.bodyValue(requestBody).retrieve() // 비밀번호 json 전달 및 응답대기
					.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
							response -> response.createException()) 
					//400~ 500 에러가 발생하면 예외처리, 이문장을 넣지 않으면 webClient가 예외가 발생해도 성공으로 간주
					.toBodilessEntity().block(); //상태코드 대기 및 동기 통신으로 전환 - 트랜젝션 완료 

		} catch (WebClientResponseException e) {
			// 4xx/5xx 응답 에러 처리
			throw new RuntimeException("James 서버 계정 생성 실패: " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			// 기타 통신 오류 처리
			throw new RuntimeException("James 서버와 통신 중 오류 발생", e);
		}
	}

	
	/**
	 * James 서버에 등록된 계정인지 확인하고 비밀번호를 검증합니다. POST /users/{username}/verify 엔드포인트 사용
	 * 
	 * @param jamesUsername James 서버 계정 (예: user@localhost.com)
	 * @param rawPassword   암호화되지 않은 비밀번호
	 * @return 인증 성공 시 true, 실패 시 false
	 */
	public boolean authenticateUser(String jamesUsername, String rawPassword) {

		String requestBody = String.format("{\"password\": \"%s\"}", rawPassword);

		try {
			this.webClient.post().uri("/users/{username}/verify", jamesUsername).bodyValue(requestBody).retrieve()
					.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
							response -> response.createException())
					.toBodilessEntity().block();

			// 204 No Content 또는 200 OK 응답이 오면 성공으로 간주
			return true;

		} catch (WebClientResponseException.NotFound | WebClientResponseException.BadRequest e) {
			// 404 (유저 없음) 또는 400 (인증 실패) 응답 시
			return false;
		} catch (Exception e) {
			// 기타 통신 오류 처리
			throw new RuntimeException("James 서버 인증 중 오류 발생", e);
		}
	}
	
	//////////////////////////// 필요없음
	
	public void createMailbox(String jamesUsername, String mailboxName) {
		try {
			this.webClient.put()
					.uri("/users/{username}/mailboxes/{mailboxName}", jamesUsername, mailboxName) 
					// PUT /users/'user@company.com'/mailboxes/'INBOX' 호출
					.retrieve()
					.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
							response -> response.createException())
					// 400~ 500 에러가 발생하면 예외처리
					.toBodilessEntity()
					.block(); // 상태코드 대기 및 동기 통신으로 전환
					
		} catch (WebClientResponseException.Conflict e) {
			// 409 Conflict: 이미 존재하는 메일박스 (에러가 아닌 정상 상황으로 처리)
			System.out.println("메일박스 이미 존재: " + mailboxName + " (" + jamesUsername + ")");
			
		} catch (WebClientResponseException e) {
			// 4xx/5xx 응답 에러 처리
			throw new RuntimeException("James 서버 메일박스 생성 실패: " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			// 기타 통신 오류 처리
			throw new RuntimeException("James 서버와 통신 중 오류 발생", e);
		}
	}

	

}