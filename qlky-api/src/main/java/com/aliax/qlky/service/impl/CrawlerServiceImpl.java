package com.aliax.qlky.service.impl;

import com.aliax.qlky.bean.qlky.CrawlerQueryBean;
import com.aliax.qlky.entity.CrawlerDataEntity;
import com.aliax.qlky.entity.CrawlerEntity;
import com.aliax.qlky.entity.CrawlerSystemMessage;
import com.aliax.qlky.mapper.CrawlerMapper;
import com.aliax.qlky.service.CrawlerDataFieldService;
import com.aliax.qlky.service.CrawlerDataService;
import com.aliax.qlky.service.CrawlerService;
import com.aliax.qlky.service.CrawlerSystemMessageService;
import com.aliax.qlky.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
/**
 * @author 艾莉希雅
 */
@Service
public class CrawlerServiceImpl extends ServiceImpl<CrawlerMapper, CrawlerEntity>
        implements CrawlerService {
    @Autowired
    private CrawlerDataFieldService crawlerDataFieldService;
    @Autowired
    private CrawlerDataService crawlerDataService;
    @Autowired
    private CrawlerSystemMessageService crawlerSystemMessageService;
    @Autowired
    private CrawlerMapper crawlerMapper;


    @Override
    public Map<String, Object> getCrawlerDataByCrawlerId(CrawlerDataEntity crawlerDataEntity) {
        CrawlerEntity crawlerServiceById = this.getById(crawlerDataEntity.getCrawlerId());
        Map<String, Object> dataMap = new HashMap<>(20);
        CrawlerQueryBean crawlerQueryBean = new CrawlerQueryBean();
        crawlerQueryBean.setCrawlerId(crawlerDataEntity.getCrawlerId());
        crawlerQueryBean.setPageSize(crawlerDataEntity.getPageSize());
        crawlerQueryBean.setCurrentPage(crawlerDataEntity.getCurrentPage());
        Page<Map<String, Object>> crawlerDataByCrawlerId = crawlerDataService.getCrawlerDataByCrawlerId(crawlerQueryBean);
        dataMap.put("list", crawlerDataByCrawlerId.getRecords());
        dataMap.put("total", crawlerDataByCrawlerId.getTotal());
        //生成表单数据
        Map<String, Object> retMap = new HashMap<>(20);
        Map<String, Object> tableDataByCrawlerId = crawlerDataService.getTableDataByCrawlerId(crawlerDataEntity.getCrawlerId());
        tableDataByCrawlerId.put("title", crawlerServiceById.getCrawlerName());
        retMap.put("crawlerData", dataMap);
        retMap.put("tableData", tableDataByCrawlerId);
        return retMap;
    }

    @Override
    public Page<CrawlerEntity> selectWithCrawlerName(CrawlerEntity crawlerEntity) {
        return PageUtil.getPage(crawlerEntity, (param) -> crawlerMapper.selectCrawlerList(param));
    }

    @Override
    public void executeCrawler(CrawlerEntity crawlerEntity) {
        //保存信息开始执行
        CrawlerSystemMessage startMessage = new CrawlerSystemMessage();
        startMessage.setMessage(
                crawlerEntity.getCrawlerName()+"开始执行" + "时间：" + new Date());
        startMessage.setCreatetime(new Date());
        startMessage.setUpdatetime(new Date());
        crawlerSystemMessageService.save(startMessage);
        //统计开始时间
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            String scriptPath = crawlerEntity.getCrawlerProgramPath();
            File scriptFile = new File(scriptPath);
            File scriptDirectory = scriptFile.getParentFile();
            if (!scriptDirectory.exists() || !scriptDirectory.isDirectory()) {
                throw new IOException("无效的脚本目录: " + scriptDirectory.getAbsolutePath());
            }
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptFile.getName())
                    .directory(scriptDirectory);  // 关键：设置工作目录
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            // 异步读取输出
            CompletableFuture<String> outputFuture = CompletableFuture.supplyAsync(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor);
            // 异步等待进程结束
            CompletableFuture<Integer> exitFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return process.waitFor();
                } catch (InterruptedException e) {
                    throw new CompletionException(e);
                }
            }, executor);
            // 组合结果
            CompletableFuture.allOf(outputFuture, exitFuture)
                    .thenAccept((v) -> {
                        int exitCode = exitFuture.join();
                        String output = outputFuture.join();
                        CrawlerSystemMessage crawlerSystemMessage = new CrawlerSystemMessage();
                        crawlerSystemMessage.setMessage(
                                crawlerEntity.getCrawlerName()+"爬虫执行结果："
                                        + Arrays.toString(output.split("\n"))
                        );
                        crawlerSystemMessage.setCreatetime(new Date());
                        crawlerSystemMessage.setUpdatetime(new Date());
                        //存储到数据库
                        crawlerSystemMessageService.save(crawlerSystemMessage);
                        if (exitCode != 0) {
                            throw new CompletionException(
                                    new RuntimeException(
                                            String.format("Python脚本执行失败，退出码: %d\n输出: %s",
                                                    exitCode, output)
                                    )
                            );
                        }
                    })
                    .get(30, TimeUnit.SECONDS); // 设置超时
        } catch (TimeoutException e) {
            throw new RuntimeException("Python脚本执行超时");
        } catch (ExecutionException e) {
            throw new RuntimeException("执行Python脚本时出错: " + e.getCause().getMessage());
        } catch (Exception e) {
            throw new RuntimeException("执行Python脚本时出错: " + e.getMessage());
        } finally {
            //任务结束信息
            CrawlerSystemMessage endMessage = new CrawlerSystemMessage();
            endMessage.setMessage(
                    crawlerEntity.getCrawlerName()+"结束，"
                    //时间
                    +"耗时:"+ (System.currentTimeMillis() - startTime)
            );
            endMessage.setCreatetime(new Date());
            endMessage.setUpdatetime(new Date());
            crawlerSystemMessageService.save(endMessage);
            executor.shutdownNow();
        }
    }
    @Override
    public void killPythonByPathEnhanced(Integer crawlerId) throws IOException {
        CrawlerEntity crawlerEntity = crawlerMapper.selectById(crawlerId);
        String os = System.getProperty("os.name").toLowerCase();
        String pypath = crawlerEntity.getCrawlerProgramPath();
        pypath = Path.of(pypath).getFileName().toString();
        ProcessBuilder psBuilder;
        if (os.contains("win")) {
            String wmicCmd = String.format(
                    "wmic process where \"name='python.exe' and commandline like '%%%s%%'\" get processid",
                    pypath);
            psBuilder = new ProcessBuilder("cmd", "/c", "chcp 65001 >nul && " + wmicCmd);
            psBuilder.environment().put("LC_ALL", "zh_CN.GBK");
        } else {
            psBuilder = new ProcessBuilder("pgrep", "-f", "--exact", pypath);
        }
        try {
            Process psProcess = psBuilder.start();
            String encoding = os.contains("win") ? "GBK" : "UTF-8";
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(psProcess.getInputStream(), encoding))) {

                br.readLine(); // 跳过WMIC标题行
                String line;
                while ((line = br.readLine()) != null) {
                    String pid = line.trim();
                    if (!pid.isEmpty()) {
                        killProcess(pid, os.contains("win"));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("进程终止失败: " + e.getMessage());
        }
    }
    // 改进的Windows PID解析方法
    private String parseWindowsPid(String tasklistLine) {
        return Arrays.stream(tasklistLine.split("\\s+"))
                .filter(s -> s.matches("\\d+"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的PID"));
    }

    // 跨平台进程终止方法
    private void killProcess(String pid, boolean isWindows) throws IOException {
        ProcessBuilder killBuilder = isWindows
                ? new ProcessBuilder("taskkill", "/F", "/PID", pid)
                : new ProcessBuilder("kill", "-9", pid);

        killBuilder.redirectErrorStream(true); // 合并错误流[8](@ref)
        Process killProcess = killBuilder.start();
        StringBuffer logger = new StringBuffer();
        // 消费输出流防止阻塞[5](@ref)
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(killProcess.getInputStream()))) {
                while (true){
                    String s = reader.readLine();
                    if (s == null) break;
                    logger.append(s);
                };
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        log.debug("killProcess:" + logger);
    }


}




