package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.FileInfo;
import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.config.cantants.SystemConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class FileUploadController {


    // 允许的文件类型白名单
    private static final String[] ALLOWED_EXTENSIONS = { 
        "jpg", "jpeg", "png", "gif", 
        "pdf", "doc", "docx", "xls", "xlsx","py"
    };

    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostMapping("/uploadFile")
    public HttpResult uploadFile(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category) {
        
        try {
            // 1. 基础校验
            if (file.isEmpty()) {
                return HttpResult.fail("上传文件不能为空");
            }

            // 2. 文件大小校验
            if (file.getSize() > MAX_FILE_SIZE) {
                return HttpResult.fail("文件大小不能超过10MB");
            }

            // 3. 文件类型校验
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            if (!isAllowedExtension(fileExtension)) {
                return HttpResult.fail("不支持的文件类型");
            }
            // 4. 创建存储目录
            Path uploadPath = Paths.get(SystemConstants.IMAGE_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // 5. 生成唯一文件名
            String safeFilename = generateSafeFilename(originalFilename);
            Path filePath = uploadPath.resolve(safeFilename);
            // 6. 保存文件
            file.transferTo(filePath.toFile());
            // 生成baseUrl
            String baseUrl = buildBaseUrl(request);

            // 创建FileInfo时传入baseUrl
            FileInfo fileInfo = new FileInfo(
                    originalFilename,
                    safeFilename,
                    file.getContentType(),
                    file.getSize(),
                    "/data/image" + "/" + safeFilename,
                    category,
                    baseUrl  // 传入动态生成的基础URL
            );

            return HttpResult.success(fileInfo);
        } catch (IOException e) {
            return HttpResult.fail("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return HttpResult.fail("系统错误：" + e.getMessage());
        }
    }

    // 获取文件扩展名
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1).toLowerCase();
    }

    // 校验文件类型
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    // 生成安全文件名
    private String generateSafeFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(originalFilename);
        return uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    // 构建基础URL的方法
    private String buildBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort() +
                request.getContextPath();
    }
}

