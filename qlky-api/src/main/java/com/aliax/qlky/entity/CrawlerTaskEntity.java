package com.aliax.qlky.entity;

import com.aliax.qlky.bean.basebean.PageBean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.io.Serializable;

/**
 * (CrawlerTask)实体类
 *
 * @author makejava
 * @since 2025-02-11 22:43:49
 */
@TableName("crawler_task")
public class CrawlerTaskEntity extends PageBean<CrawlerTaskEntity> implements Serializable {
    private static final long serialVersionUID = -85959931641186550L;
    /**
     * 任务ID，主键，自动递增
     */
    @TableId(value ="task_id", type = IdType.AUTO)
    private Integer taskId;
    /**
     * 关联爬虫ID，表示该任务属于哪个爬虫
     */
    private Integer crawlerId;
    /**
     * 任务名称，标识该任务的名字
     */
    private String taskName;
    /**
     * 任务状态，表示任务的当前状态
     */
    private String taskStatus;
    /**
     * 任务开始时间，记录任务启动的时间
     */
    private Date startTime;
    /**
     * 任务结束时间，记录任务完成的时间
     */
    private Date endTime;
    /**
     * 创建时间，默认当前时间
     */
    private Date createdAt;
    /**
     * 更新时间，自动更新
     */
    private Date updatedAt;


    /**
     * 时间表达式
     */
    private String cron;

    /**
     * 间隔时间秒
     */
    private Integer intervalSeconds;


    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}

