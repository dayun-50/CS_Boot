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

	// 연락처 목록 조회
	@GetMapping("/list")
	public ResponseEntity<List<ContactDTO>> getMyContacts() {
		return ResponseEntity.ok(contactService.getMyContacts());
	}

	// 연락처 등록
	@PostMapping("/insert")
	public ResponseEntity<Integer> insertContact(@RequestBody ContactDTO dto) {
		int rowsInserted = contactService.insertContact(dto);
		if (rowsInserted > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(rowsInserted);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	// 연락처 수정 (share 포함) - 분류 버튼 클릭 시에도 적용
	@PutMapping("/update")
	public ResponseEntity<Integer> updateContact(@RequestBody ContactDTO dto) {
		boolean updated = contactService.updateContact(dto);
		if (updated) {
			return ResponseEntity.ok(1);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
	}

	// 연락처 삭제
	@DeleteMapping("/delete/{contact_seq}")
	public ResponseEntity<Integer> deleteContact(@PathVariable int contact_seq) {
		int rowsDeleted = contactService.deleteContact(contact_seq);
		if (rowsDeleted > 0) {
			return ResponseEntity.ok(rowsDeleted);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
	}
}