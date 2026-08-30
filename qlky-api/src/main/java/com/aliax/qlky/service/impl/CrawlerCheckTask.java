package com.aliax.qlky.service.impl;

import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.aliax.qlky.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrawlerCheckTask {
    @Autowired
    private CrawlerService crawlerExecutor; // 注入你的爬虫执行器
    @Autowired
    private CrawlerTaskQueue taskQueue; // 任务队列

    @Autowired
    private CrawlerSchedulerService crawlerSchedulerService; //任务调度器

    /**
     * 每5分钟执行一次待执行任务[1,4](@ref)
     * 使用 fixedRate 确保固定频率执行
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkAndExecuteCrawlers() {
        List<CrawlerTaskEntity> pendingTasks = taskQueue.getPendingTasks(); // 获取待执行任务
        if (!pendingTasks.isEmpty()) {
            pendingTasks.forEach(crawlerTask -> {
                //已经结束返回
                long now = System.currentTimeMillis();
                if (crawlerTask.getStartTime() != null && crawlerTask.getEndTime() != null) {
                    long startTime = crawlerTask.getStartTime().getTime();
                    long endTime = crawlerTask.getEndTime().getTime();
                    // 当前时间在任务时间窗口之外时停止
                    if (now < startTime || now > endTime) {
                        crawlerSchedulerService.stopTask(crawlerTask.getCrawlerId());
                        return;
                    }
                }
                if (crawlerTask.getCron() != null) {
                    crawlerSchedulerService.startCronTask(crawlerTask.getTaskId(), crawlerTask.getCron());
                    taskQueue.markAsCompleted(crawlerTask.getTaskId());
                } else if (crawlerTask.getIntervalSeconds() != null) {
                    crawlerSchedulerService.startIntervalTask(crawlerTask.getTaskId(), crawlerTask.getIntervalSeconds());
                    taskQueue.markAsCompleted(crawlerTask.getTaskId());
                }
            });
        }
    }
}
