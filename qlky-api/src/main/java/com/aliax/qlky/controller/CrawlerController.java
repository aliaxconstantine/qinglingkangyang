package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.config.cantants.SystemConstants;
import com.aliax.qlky.entity.CrawlerEntity;
import com.aliax.qlky.service.CrawlerDataFieldService;
import com.aliax.qlky.service.CrawlerDataService;
import com.aliax.qlky.service.CrawlerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
public class CrawlerController {
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerDataFieldService crawlerDataFieldService;
    @Autowired
    private CrawlerDataService crawlerDataService;


    @RequestMapping("/getCrawler")
    public HttpResult getCrawler(@RequestParam("crawlerId") Integer crawlerId) {
        CrawlerEntity crawlerEntity = crawlerService.getById(crawlerId);
        return HttpResult.success(crawlerEntity);
    }

    @RequestMapping("/getCrawlerList")
    public HttpResult getCrawlerList(@RequestBody CrawlerEntity crawlerEntity) {
        Page<CrawlerEntity> withCrawlerName = crawlerService.selectWithCrawlerName(crawlerEntity);
        return HttpResult.success(withCrawlerName.getRecords(), withCrawlerName.getTotal());
    }


    @RequestMapping("/submitCrawler")
    public HttpResult submitCrawler(@RequestParam(value = "file", required = false) MultipartFile file,
                                    CrawlerEntity crawlerEntity) throws IOException {
        // 检查文件是否为空
        if (file != null && !file.isEmpty()) {
            CrawlerEntity oldCrawler = crawlerEntity.getCrawlerId() == null ? null : crawlerService.getById(crawlerEntity.getCrawlerId());
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                return HttpResult.fail("上传文件名称不能为空");
            }
            Path uploadDirectory = Path.of(SystemConstants.CRAWLER_UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadDirectory);
            String safeFileName = Path.of(originalFileName).getFileName().toString();
            Path destination = uploadDirectory.resolve(UUID.randomUUID() + "-" + safeFileName).normalize();
            file.transferTo(destination);
            crawlerEntity.setCrawlerProgramPath(destination.toString());
            if (!crawlerService.saveOrUpdate(crawlerEntity)) {
                Files.deleteIfExists(destination);
                return HttpResult.fail("保存爬虫失败");
            }
            boolean oldFileDeleted = oldCrawler == null || deleteManagedCrawlerFile(oldCrawler.getCrawlerProgramPath());
            return oldFileDeleted ? HttpResult.success("上传成功") : HttpResult.success("上传成功，旧上传文件清理失败");
        }
        if (file == null || file.isEmpty()) {
            crawlerService.saveOrUpdate(crawlerEntity);
            return HttpResult.success("保存成功");
        }
        // 获取上传文件的原始名称
        String fileName = file.getOriginalFilename();
        //如果是修改，路径一致
        //如果是修改，且路径不一致，删除旧的文件
        if (fileName != null && crawlerEntity.getCrawlerId() != null && !fileName.equals(crawlerEntity.getCrawlerProgramPath())) {
            CrawlerEntity oldCrawler = crawlerService.getById(crawlerEntity.getCrawlerId());
            if (oldCrawler != null) {
                String oldFilePath = oldCrawler.getCrawlerProgramPath();
                if(oldFilePath != null){
                    String path = Path.of(oldFilePath).toString();
                    File oldFile = new File(path);
                    if (oldFile.exists()) {
                        boolean deleted = oldFile.delete();
                        if (!deleted) {
                            return HttpResult.fail("删除旧文件失败");
                        }
                    }
                }
            }
        }
        // 获取 crawler_program_path 字段指定的路径
        String uploadPath = crawlerEntity.getCrawlerProgramPath();
        String path = Path.of(SystemConstants.FILE_UPLOAD_DIR, uploadPath).toString();
        // 如果路径不存在，则创建
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            boolean mkdirs = uploadDir.mkdirs();
            if (!mkdirs) {
                return HttpResult.fail("创建文件夹失败");
            }
        }
        // 构建文件保存路径
        String filePath = path + File.separator + fileName;
        crawlerEntity.setCrawlerProgramPath(filePath);
        // 将文件保存到指定路径
        File dest = new File(filePath);
        file.transferTo(dest);
        crawlerService.saveOrUpdate(crawlerEntity);
        return HttpResult.success("上传成功！");
    }

    //删除爬虫
    private boolean isManagedCrawlerFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return false;
        }
        Path uploadDirectory = Path.of(SystemConstants.CRAWLER_UPLOAD_DIR).toAbsolutePath().normalize();
        Path candidate = Path.of(filePath).toAbsolutePath().normalize();
        return candidate.startsWith(uploadDirectory);
    }

    private boolean deleteManagedCrawlerFile(String filePath) {
        if (!isManagedCrawlerFile(filePath)) {
            return true;
        }
        try {
            return Files.deleteIfExists(Path.of(filePath));
        } catch (IOException exception) {
            return false;
        }
    }

    @RequestMapping("/deleteCrawler")
    public HttpResult deleteCrawler(@RequestBody CrawlerEntity crawlerEntity) {
        QueryWrapper<CrawlerEntity> crawlerEntityQueryWrapper = new QueryWrapper<>();
        crawlerEntityQueryWrapper.eq("crawler_id", crawlerEntity.getCrawlerId());
        CrawlerEntity one = crawlerService.getOne(crawlerEntityQueryWrapper);
        if (one == null) {
            return HttpResult.fail("爬虫不存在！");
        }
        // 获取 crawler_program_path 字段指定的路径
        String uploadPath = one.getCrawlerProgramPath();
        // 删除文件
        deleteManagedCrawlerFile(uploadPath);
        QueryWrapper<CrawlerEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("crawler_id", crawlerEntity.getCrawlerId());
        crawlerService.remove(objectQueryWrapper);
        return HttpResult.success("删除成功！");
    }

    //修改爬虫路径
    @RequestMapping("/updateCrawler")
    public HttpResult updateCrawler(@RequestBody CrawlerEntity crawlerEntity) {
        // 获取 crawler_program_path 字段指定的路径
        String newUploadPath = crawlerEntity.getCrawlerProgramPath();

        // 获取数据库中原来的路径
        CrawlerEntity oldCrawler = crawlerService.getById(crawlerEntity.getCrawlerId());
        String oldUploadPath = oldCrawler.getCrawlerProgramPath();

        if (!isManagedCrawlerFile(oldUploadPath)) {
            oldCrawler.setCrawlerProgramPath(newUploadPath);
            crawlerService.updateById(oldCrawler);
            return HttpResult.success("爬虫路径已更新");
        }

        // 判断旧路径和新路径是否相同
        if (oldUploadPath.equals(newUploadPath)) {
            return HttpResult.success("新路径与旧路径相同，无需更新");
        }

        File oldFile = new File(oldUploadPath);
        File newFile = new File(newUploadPath);

        try {
            // 检查文件是否存在并且不是目录
            if (oldFile.exists() && !oldFile.isDirectory()) {
                // 如果目标路径的父目录不存在，则创建父目录
                boolean mkdirs = newFile.getParentFile().mkdirs();
                // 复制文件
                Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // 删除旧文件
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    return HttpResult.fail("删除旧文件失败");
                }
                // 更新数据库中的路径信息
                oldCrawler.setCrawlerProgramPath(newUploadPath);
                crawlerService.updateById(oldCrawler);
                // 返回成功响应
                return HttpResult.success("文件已成功更新");

            } else {
                return HttpResult.fail("旧文件不存在或是目录，无法进行复制");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return HttpResult.fail("文件操作发生异常：" + e.getMessage());
        }
    }

    //修改爬虫信息
    @RequestMapping("/updateCrawlerInfo")
    public HttpResult updateCrawlerInfo(@RequestBody CrawlerEntity crawlerEntity) {
        crawlerService.updateById(crawlerEntity);
        return HttpResult.success("修改成功！");
    }
}
