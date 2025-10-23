package com.kedu.project.contact;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // HttpStatus import 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/contact")
@RestController
public class ContactController {
	@Autowired
	private ContactService contactService;

	// 주소록 리스트
	@GetMapping("/list")
	public ResponseEntity<List<ContactDTO>> getMyContacts() {
		List<ContactDTO> contactList = contactService.getMyContacts();
		return ResponseEntity.ok(contactList);
	}

	// 분류 버튼 값 수정
	@PostMapping("/share/{contact_seq}")
	public ResponseEntity<String> updateContactGroup(@PathVariable int contact_seq,
			@RequestBody Map<String, String> body) {

		String share = body.get("share");
		boolean updated = contactService.updateContactGroup(contact_seq, share);

		if (updated) {
			return ResponseEntity.ok("변경 성공");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("변경 실패");
		}
	}

	// 주소록 등록
	@PostMapping("/insert")
	public ResponseEntity<Integer> insertContact(@RequestBody ContactDTO dto) {
		System.out.println("Received ContactDTO: " + dto); // 디버깅용 출력
		int rowsInserted = contactService.insertContact(dto);
		if (rowsInserted > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(rowsInserted);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	// 주소록 수정 (ResponseEntity 사용하도록 수정)
	@PutMapping("/update")
	public ResponseEntity<Integer> updateContact(@RequestBody ContactDTO dto) {
		int rowsUpdated = contactService.updateContact(dto);
		if (rowsUpdated > 0) {
			return ResponseEntity.ok(rowsUpdated); // 200 OK
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0); // 404 Not Found
		}
	}

	// 주소록 삭제 (ResponseEntity 사용하도록 수정)
	@DeleteMapping("/delete/{contact_seq}")
	public ResponseEntity<Integer> deleteContact(@PathVariable int contact_seq) {
		int rowsDeleted = contactService.deleteContact(contact_seq);
		if (rowsDeleted > 0) {
			return ResponseEntity.ok(rowsDeleted); // 200 OK
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0); // 404 Not Found
		}
	}
}