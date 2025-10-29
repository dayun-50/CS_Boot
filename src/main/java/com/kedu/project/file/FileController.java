package com.kedu.project.file;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

	/*
	 * @GetMapping("/download") public ResponseEntity<InputStreamResource> download(
	 * 
	 * @RequestParam String sysname,
	 * 
	 * @RequestParam String file_type) { return fileService.streamDownload(sysname,
	 * file_type); }
	 */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(String sysname, String file_type) throws Exception {
        Map<String, Object> data = fileService.getFileStream(sysname, file_type);
        if (data == null) return ResponseEntity.notFound().build();

        String oriName = (String) data.get("oriName");
        InputStream stream = (InputStream) data.get("stream");
        String encoded = URLEncoder.encode(oriName, "UTF-8").replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", encoded);

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }
}
