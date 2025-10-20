package com.kedu.project.contact;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
 *  연락처 기능 구현 Controller
 */

@RequestMapping("/contact")
@RestController
public class ContactController {
	@Autowired
	private ContactService contactService;
	// 알아 고쳐쓸려면 알아서 고쳐쓰소
	
	// 주소록 리스트
	@GetMapping("/list")
	public ResponseEntity<List<ContactDTO>> getMyContacts() {
		List<ContactDTO> contactList = contactService.getMyContacts(); // 실제 DB 조회
		return ResponseEntity.ok(contactList);
	}
	
	// 주소록 등록
	@PostMapping("/insert")
	public ResponseEntity<Integer> insertContact(@RequestBody ContactDTO dto) {
		int noticeInsert = contactService.insertContact(dto);
		return ResponseEntity.ok(noticeInsert);
	}

	// 주소록 수정
	@PutMapping("/update")
	public int updateContact(@RequestBody ContactDTO dto) {
		return contactService.updateContact(dto);
	}

	// 주소록 삭제
	@DeleteMapping("/delete/{notice_seq}")
	public int deleteContact(@PathVariable int contact_seq) {
		return contactService.deleteContact(contact_seq);
	}
}
