/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-20 11:32:47
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-20 11:37:08
 * @FilePath: \build-one\src\main\java\com\creation\platform\controller\FileController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%9D%97%7D
 */
package com.creation.platform.controller;

import com.creation.platform.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${server.port:45757}")
    private int serverPort;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名不能为空");
        }

        String ext = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            ext = originalFilename.substring(dotIndex);
        }

        String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File targetFile = new File(uploadDir + newFileName);
        try {
            file.transferTo(targetFile);
            String fileUrl = "http://localhost:" + serverPort + "/uploads/" + newFileName;
            return Result.success("上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}
