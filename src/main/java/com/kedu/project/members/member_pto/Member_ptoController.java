package com.kedu.project.members.member_pto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 		남은 연차 관리 controller
 * */

@RequestMapping("/pto")
@RestController
public class Member_ptoController {
	@Autowired
	private Member_ptoService member_ptoService;
	
	//남은 연차 가져오는 로직
	@GetMapping
	public ResponseEntity<Map <String, Object>> getLeftPto(){
		String member_email = "test01@test.com"; // 추후 토큰에서 추출 예정		
		Member_ptoDTO dto =member_ptoService.getLeftPto(member_email);
		int totalPto = member_ptoService.standardPto(member_email);
		System.out.println("남은연차"+totalPto);
		
		Map<String, Object> result = new HashMap<>();
		result.put("dto", dto);
		result.put("totalPto", totalPto);
		
		return ResponseEntity.ok(result);
	}
	
	
	
	
	
}
