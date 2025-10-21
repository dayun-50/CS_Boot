package com.kedu.project.file;


/*
 	디비 파일타입에 들어가는 상수값 정리함
 	
 	각 서비스에서 FileDAO 콜시 setFile_type(); 이용해서 파일 종류 넣어쓰시오~
 	본인 파트부분만 외워서 쓰면됩니다 다른분들껀 건들지마세욥
*/
public class FileConstants {
	public static final String FE = "email"; // 이메일용
	public static final String FC = "chat"; // 채팅용
	public static final String FB = "borad"; // 공지사항용
	public static final String FA = "approval"; //전자결재용
}	
