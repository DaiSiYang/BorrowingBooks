package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@Slf4j
public class FileUploadController {
    @PostMapping("/public/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        log.info("文件上传开始");
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = AliOssUtil.uploadFile(filename,file.getInputStream());
        return Result.ok(url);
    }
}
