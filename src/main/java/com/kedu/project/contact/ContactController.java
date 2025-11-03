package com.kedu.project.contact;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kedu.project.members.member.MemberService;

// 연락처
@RequestMapping("/contact")
@RestController
public class ContactController {

	@Autowired
	private ContactService contactService;


	// 본인 연락처 목록 조회
	@GetMapping("/list/{owner_email}")
	public ResponseEntity<List<ContactDTO>> getMyContacts(@PathVariable String owner_email) {
		List<ContactDTO> contacts = contactService.getContactsByOwner(owner_email);
		return ResponseEntity.ok(contacts);
	}

	// 연락처 등록
	@PostMapping("/insert")
	public ResponseEntity<Integer> insertContact(@RequestBody ContactDTO dto) {
		if (dto.getOwner_email() == null || dto.getOwner_email().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}

		// 로그인한 사용자 company_code 자동 세팅
		String companyCode = contactService.getCompanyCodeByEmail(dto.getOwner_email());
		dto.setCompany_code(companyCode);
		System.out.println(dto);

		int rowsInserted = contactService.insertContact(dto);
		if (rowsInserted > 0) {
			return ResponseEntity.ok(1);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	// 연락처 수정
	@PutMapping("/update")
	public ResponseEntity<?> updateContact(@RequestBody ContactDTO dto) {
		boolean updated = contactService.updateContact(dto);
		if (updated) {
			return ResponseEntity.ok().body(Map.of("success", true));
		} else {
			// Service/DAO/Mapper에서 업데이트가 실패했다면 (WHERE 조건 불일치 등)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "Update failed (Check contact_seq/owner_email)"));
		}
	}

	// 연락처 삭제
	@DeleteMapping("/delete")
	public ResponseEntity<Integer> deleteContact(@RequestBody ContactDTO dto) {
		int rowsDeleted = contactService.deleteContactByOwner(dto.getContact_seq(), dto.getOwner_email());
		return rowsDeleted > 0 ? ResponseEntity.ok(rowsDeleted) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
	}

	// 개인용
	// GET /contact/individual/{owner_email}
	@GetMapping("/individual/{owner_email}")
	public ResponseEntity<List<ContactDTO>> getIndividualContacts(@PathVariable("owner_email") String ownerEmail) {
		List<ContactDTO> contacts = contactService.selectIndividualContacts(ownerEmail);
		return ResponseEntity.ok(contacts);
	}

	// 팀원용
	// 팀 전체 연락처 조회 (공유 여부 상관없이)
	@GetMapping("/team/{owner_email}")
	public ResponseEntity<List<ContactDTO>> getTeamContacts(@PathVariable("owner_email") String ownerEmail) { // ownerEmail만
																												// 받습니다.

		// 💡 1. MemberService를 사용하여 현재 사용자의 부서 코드(dept_code)를 조회합니다.
		String deptCode = contactService.getDeptCodeByEmail(ownerEmail);

		// 만약 부서 코드를 가져오지 못했다면 빈 목록을 반환합니다.
		if (deptCode == null || deptCode.isEmpty()) {
			return ResponseEntity.ok(List.of());
		}

		// 💡 2. Map에 dept_code와 ownerEmail을 담아 DAO로 전달합니다.
		// teamService가 아닌 ContactService를 사용하므로 Map을 만듭니다.
		Map<String, Object> params = Map.of("dept_code", deptCode, "owner_email", ownerEmail);

		List<ContactDTO> contacts = contactService.selectTeamContact(params);
		return ResponseEntity.ok(contacts);
	}

}
