package com.kedu.project.chatting.chat_message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.chatting.chat_member.Chat_memberDAO;
import com.kedu.project.chatting.chat_member.Chat_memberDTO;
import com.kedu.project.file.FileConstants;
import com.kedu.project.file.FileDAO;
import com.kedu.project.file.FileDTO;
import com.kedu.project.members.member.MemberDAO;


/*
	채팅방 메세지 기능 구현 Service
 */
@Service
public class Chat_messageService {
	@Autowired
	private Chat_messageDAO dao;

	@Autowired
	private Chat_memberDAO cMemberDao;

	@Autowired
	private MemberDAO memberDao;

	@Autowired
	private FileDAO fileDao;

	// 메세지 DB저장
	public int messageInsert(Chat_messageDTO dto) {
		return dao.messageInsert(dto);
	}
	// 파일 DB저장
	public Chat_messageDTO fileInsert(Chat_messageDTO dto) {
		return dao.fileInsert(dto);
	}

	// 방 seq에 따른 채팅내역 출력
	public List<Chat_messageDTO> getMessageBySeq(int chat_seq){
		List<Chat_messageDTO> messages = dao.getMessageBySeq(chat_seq);
		return messages;
	}

	// 채팅방 마지막 메세지 시퀀스값 출력
	public int lastMessageSeq(int chatSeq) {
		return dao.lastMessageSeq(chatSeq);
	}

	// 채팅방 마지막 메세지 시퀀스 입력
	public int updateLastMessageSeq(String email, int messageSeq, int chatSeq) {
		Chat_memberDTO dto = new Chat_memberDTO();
		dto.setLast_message_seq(messageSeq);
		dto.setMember_email(email);
		dto.setChat_seq(chatSeq);

		return cMemberDao.updateLastMessageSeq(dto);
	}

	// 채팅방 멤버 뽑아오기
	public List<String> getMembersByRoomSeq(int chat_seq){
		return cMemberDao.getMembersByRoomSeq(chat_seq);
	}

	// 마지막으로 읽은 메세지 시퀀스 뽑기
	public int getLastMessageSeq(String email, int chatSeq) {
		return cMemberDao.getLastMessageSeq(email,chatSeq);
	}

	//메세지 시퀀스로 파일 dto가져오기
	public FileDTO getFileByChatSeq(int parent_seq, String file_type){
		Map<String, Object> param =new HashMap<>();
		param.put("parent_seq", parent_seq);
		param.put("file_type", file_type);

		return fileDao.getFileByChatSeq(param);
	}

	// 메세지 글자로 검색
	public Map<String, Object>serchByText(Chat_messageDTO dto){
		List<Chat_messageDTO> messages = dao.serchByText(dto);
		List<FileDTO> files = new ArrayList<>();
		for (Chat_messageDTO mdto : messages) {
		    FileDTO temp = getFileByChatSeq(mdto.getMessage_seq(),FileConstants.FC);
		    files.add(temp);
		}
		List<Map<String, Object>> mergedList = new ArrayList<>();

		for (int i = 0; i < messages.size(); i++) {
		    Map<String, Object> entry = new HashMap<>();
		    entry.put("data", messages.get(i));
		    entry.put("fdata", files.get(i) != null ? files.get(i) : null);
		    mergedList.add(entry);
		}

		// 4. 최종 Map 생성
		Map<String, Object> finalResult = new HashMap<>();
		finalResult.put("type", "history");
		finalResult.put("each", mergedList);
		
		return finalResult;
	}

	// 메세지 날짜로 검색
	public Map<String, Object> serchByDate(Chat_messageDTO dto){
		List<Chat_messageDTO> messages = dao.serchByDate(dto);
		List<FileDTO> files = new ArrayList<>();
		for (Chat_messageDTO mdto : messages) {
		    FileDTO temp = getFileByChatSeq(mdto.getMessage_seq(),FileConstants.FC);
		    files.add(temp);
		}
		List<Map<String, Object>> mergedList = new ArrayList<>();

		for (int i = 0; i < messages.size(); i++) {
		    Map<String, Object> entry = new HashMap<>();
		    entry.put("data", messages.get(i));
		    entry.put("fdata", files.get(i) != null ? files.get(i) : null);
		    mergedList.add(entry);
		}

		// 4. 최종 Map 생성
		Map<String, Object> finalResult = new HashMap<>();
		finalResult.put("type", "history");
		finalResult.put("each", mergedList);
		
		return finalResult;
	}
	
	// 채팅방 멤버 이름 서칭
	public String memberNameSerch(String email) {
		return memberDao.selectMemberName(email);
	}

}
