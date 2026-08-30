package com.aliax.qlky.service.impl;

import com.aliax.qlky.config.cantants.SystemConstants;
import com.aliax.qlky.entity.CrawlerEntity;
import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.aliax.qlky.service.CrawlerService;
import com.aliax.qlky.service.CrawlerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@Service
@EnableScheduling
public class CrawlerSchedulerService implements SchedulingConfigurer {
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private ScheduledTaskRegistrar taskRegistrar; // 声明成员变量
    @Autowired
    private CrawlerService crawlerExecutor;

    @Autowired
    private CrawlerTaskService crawlerTaskService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar; // 保存实例
        // 配置线程池
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.initialize();
        taskRegistrar.setTaskScheduler(scheduler);
    }

    /**
     * 启动间隔执行任务（秒级）
     *
     * @param taskId       任务ID（用于管理）
     * @param intervalSeconds 间隔秒数
     */
    public void startIntervalTask(Integer taskId, int intervalSeconds) {
        stopTask(taskId); // 停止已有任务
        CrawlerTaskEntity taskServiceById = crawlerTaskService.getById(taskId);
        CrawlerEntity crawlerEntity = crawlerExecutor.getById(taskServiceById.getCrawlerId());
        if (crawlerEntity == null) {
            return;
        }
        ScheduledFuture<?> future = taskRegistrar.getScheduler().scheduleAtFixedRate(
                () -> crawlerExecutor.executeCrawler(crawlerEntity),
                intervalSeconds * 1000L // 转换为毫秒
        );
        tasks.put(taskId, future);
    }

    /**
     * 启动Cron表达式任务
     *
     * @param taskId      任务ID
     * @param cronExpression Cron表达式
     */
    public void startCronTask(Integer taskId, String cronExpression) {
        stopTask(taskId);
        CrawlerTaskEntity taskServiceById = crawlerTaskService.getById(taskId);
        CrawlerEntity crawlerEntity = crawlerExecutor.getById(taskServiceById.getCrawlerId());
        if (crawlerEntity == null) {
            return;
        }
        ScheduledFuture<?> future = Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(
                () -> crawlerExecutor.executeCrawler(crawlerEntity),
                new CronTrigger(cronExpression)
        );
        if (future != null) {
            tasks.put(taskId, future);
        }
    }

    /**
     * 停止任务
     *
     * @param taskId 任务ID
     */
    public void stopTask(Integer taskId) {
        ScheduledFuture<?> future = tasks.get(taskId);
        if (future != null) {
            future.cancel(true);
            tasks.remove(taskId);
        }
        //查询任务存不存在
        CrawlerTaskEntity taskServiceById = crawlerTaskService.getById(taskId);
        if (taskServiceById != null) {
            CrawlerTaskEntity crawlerTaskEntity = new CrawlerTaskEntity();
            crawlerTaskEntity.setTaskId(taskId);
            crawlerTaskEntity.setTaskStatus(SystemConstants.INACTIVE_STATE);
            crawlerTaskService.saveOrUpdate(crawlerTaskEntity);
        }
    }
}
