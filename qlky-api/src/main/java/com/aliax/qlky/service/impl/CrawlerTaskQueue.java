package com.aliax.qlky.service.impl;

import com.aliax.qlky.config.cantants.SystemConstants;
import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.aliax.qlky.service.CrawlerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrawlerTaskQueue {

    @Autowired
    private CrawlerTaskService crawlerRepository;
    // 模拟从数据库查询待执行任务
    public List<CrawlerTaskEntity> getPendingTasks() {
        return crawlerRepository.list();
    }

    // 标记任务完成
    public void markAsCompleted(Integer taskId) {
        CrawlerTaskEntity task = crawlerRepository.getById(taskId);
        if (task != null) {
            // 更新任务状态为正在执行状态
            task.setTaskStatus(SystemConstants.ACTIVE_STATE);
            crawlerRepository.saveOrUpdate(task);
        }
    }
}