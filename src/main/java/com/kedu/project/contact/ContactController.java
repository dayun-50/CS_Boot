package com.kedu.project.contact;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/contact")
@RestController
public class ContactController {

	@Autowired
	private ContactService contactService;

	// 본인 연락처 목록 조회
	@GetMapping("/list")
	public ResponseEntity<List<ContactDTO>> getMyContacts(HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		if (email == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		List<ContactDTO> contacts = contactService.getContactsByOwner(email);
		return ResponseEntity.ok(contacts);
	}

	// 연락처 등록
	@PostMapping("/insert")
	public ResponseEntity<Integer> insertContact(HttpServletRequest request, @RequestBody ContactDTO dto) {
		String email = (String) request.getAttribute("email");
		if (email == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		dto.setOwner_email(email); // 로그인한 사용자 이메일로 설정
		String companyCode = contactService.getCompanyCodeByEmail(email);
		dto.setCompany_code(companyCode);

		int rowsInserted = contactService.insertContact(dto);
		return rowsInserted > 0 ? ResponseEntity.ok(1)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
	}

	// 연락처 수정
	@PutMapping("/update")
	public ResponseEntity<?> updateContact(HttpServletRequest request, @RequestBody ContactDTO dto) {
		String email = (String) request.getAttribute("email");
		if (email == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// owner_email 검증
		if (!dto.getOwner_email().equals(email)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("success", false, "message", "Permission denied"));
		}

		boolean updated = contactService.updateContact(dto);
		return updated ? ResponseEntity.ok(Map.of("success", true))
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(Map.of("success", false, "message", "Update failed"));
	}

	// 연락처 삭제
	@DeleteMapping("/delete")
	public ResponseEntity<Integer> deleteContact(HttpServletRequest request, @RequestBody ContactDTO dto) {
		String email = (String) request.getAttribute("email");
		if (email == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!dto.getOwner_email().equals(email)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(0);
		}

		int rowsDeleted = contactService.deleteContactByOwner(dto.getContact_seq(), email);
		return rowsDeleted > 0 ? ResponseEntity.ok(rowsDeleted) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
	}

	// 개인용 연락처 조회
	@GetMapping("/individual")
	public ResponseEntity<List<ContactDTO>> getIndividualContacts(HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		if (email == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		List<ContactDTO> contacts = contactService.selectIndividualContacts(email);
		return ResponseEntity.ok(contacts);
	}

	// 팀원 연락처 조회
	@GetMapping("/team")
	public ResponseEntity<List<ContactDTO>> getTeamContacts(HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		if (email == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		String deptCode = contactService.getDeptCodeByEmail(email);
		if (deptCode == null || deptCode.isEmpty())
			return ResponseEntity.ok(List.of());

		Map<String, Object> params = Map.of("dept_code", deptCode, "owner_email", email);
		List<ContactDTO> contacts = contactService.selectTeamContact(params);
		return ResponseEntity.ok(contacts);
	}
}