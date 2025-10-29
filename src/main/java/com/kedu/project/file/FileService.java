package com.kedu.project.file;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

@Service
public class FileService {

    @Autowired
    private FileDAO fileDao;

    @Autowired
    private Storage storage; 

    @Value("${spring.cloud.gcp.storage.bucket:hwi_study}")  
    private String bucketName; // application.properties에서 가져옴

    // 1. 파일 업로드
    public void upload(MultipartFile[] files, int parent_seq, String file_type, String member_email) {
    	if (files == null || files.length == 0) return;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String oriName = file.getOriginalFilename();
                    String sysName = UUID.randomUUID() + "_" + oriName;

                    // 폴더 구조 흉내내기 (approval/uuid_filename)
                    String objectName = file_type + "/" + sysName;

                    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                                                .setContentType(file.getContentType())
                                                .build();

                    try (InputStream is = file.getInputStream()) {
                        storage.createFrom(blobInfo, is);
                        System.out.println("파일업로드완료");
                    }
                    // DB에 파일 메타데이터 저장
                    fileDao.upload(new FileDTO(0, member_email, sysName, oriName, null, file_type, parent_seq));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("파일 업로드 중 오류 발생", e);
                }
            }
        }
    }

    // 2. 리스트 가져오기: DB에서 조회 (GCS에서 조회 x)
    public List<FileDTO> getFilesByParent(int parent_seq, String file_type) {
        Map<String, Object> param = new HashMap<>();
        param.put("parent_seq", parent_seq);
        param.put("file_type", file_type);
        return fileDao.selectByParentSeq(param);
    }
    
    // 3. 종류+부모 시퀀스로 삭제하기 : 글 하나 지우면 거기에 딸린 모든 파일 삭제
    public int deleteFilesByParent(int parent_seq,String file_type ) {
    	// 1. 파일이 없다면 리턴
    	List<FileDTO> files = getFilesByParent(parent_seq, file_type);
        if (files == null || files.isEmpty()) return 0;
        // 2. GCS에서도 파일 삭제
        for (FileDTO f : files) {
            try {
                String objectName = file_type + "/" + f.getSysname();
                storage.delete(BlobId.of(bucketName, objectName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
     // 3️. DB에서도 삭제
        Map<String, Object> param = new HashMap<>();
        param.put("parent_seq", parent_seq);
        param.put("file_type", file_type);

        return fileDao.deleteByParentSeq(param);
    }
    
    //4. 유지할 파일은 제외하고 나머지만 삭제
    public void deleteFilesExcept(int parentSeq, String fileType, List<String> keepFiles) {
        Map<String, Object> param = new HashMap<>();
        param.put("parent_seq", parentSeq);
        param.put("file_type", fileType);

        List<FileDTO> existing = fileDao.selectByParentSeq(param);
        if (existing == null || existing.isEmpty()) return;

        for (FileDTO f : existing) {
            String ori = f.getOriname();
            if (keepFiles != null && keepFiles.contains(ori)) continue;

            try {
                // GCS 삭제
                String objectName = fileType + "/" + f.getSysname();
                storage.delete(BlobId.of(bucketName, objectName));

                // DB 삭제
                fileDao.deleteBySysname(f.getSysname());

                System.out.println("삭제 완료: " + ori);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("파일 삭제 중 오류 발생");
            }
        }
    }
    
    //5. 시스네임으로 다운로드
    public Map<String, Object> getFileStream(String sysname, String file_type) {
        Map<String, Object> result = new HashMap<>();
        String objectPath = file_type + "/" + sysname;
        Blob blob = storage.get(bucketName, objectPath);
        if (blob == null) return null;

        String oriName = fileDao.findOriNameBySysName(sysname);
        InputStream inputStream = new ByteArrayInputStream(blob.getContent());

        result.put("oriName", oriName);
        result.put("stream", inputStream);
        return result;
    }
	/*
	 * public ResponseEntity<InputStreamResource> streamDownload( String sysname,
	 * String file_type) { try { String objectPath = file_type + "/" + sysname; Blob
	 * blob = storage.get(bucketName, objectPath); if (blob == null) { return
	 * ResponseEntity.notFound().build(); }
	 * 
	 * String oriName = fileDao.findOriNameBySysName(sysname); String encodedName =
	 * URLEncoder.encode(oriName, "UTF-8").replaceAll("\\+", "%20");
	 * 
	 * // GCS에서 바로 InputStream으로 읽기 InputStream inputStream = new
	 * ByteArrayInputStream(blob.getContent());
	 * 
	 * HttpHeaders headers = new HttpHeaders();
	 * headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	 * headers.setContentDispositionFormData("attachment", encodedName);
	 * 
	 * // InputStreamResource로 바로 스트리밍 return ResponseEntity.ok() .headers(headers)
	 * .body(new InputStreamResource(inputStream));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return
	 * ResponseEntity.internalServerError().build(); } }
	 */

    
}
