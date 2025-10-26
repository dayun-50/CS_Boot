package com.kedu.project.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(
            @RequestParam String sysname,
            @RequestParam String file_type) {
        return fileService.streamDownload(sysname, file_type);
    }
	
}
