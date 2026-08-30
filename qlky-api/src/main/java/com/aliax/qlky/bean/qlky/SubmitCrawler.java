package com.aliax.qlky.bean.qlky;

import java.util.List;
import java.util.Map;

/**
 * 脚本提交数据
 */
public class SubmitCrawler {
    /**
     * 爬虫ID
     */
    private Integer crawlerId;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 上传数据
     */
    List<Map<String, Object>> submitCrawlerList;

    public List<Map<String, Object>> getSubmitCrawlerList() {
        return submitCrawlerList;
    }

    public void setSubmitCrawlerList(List<Map<String, Object>> submitCrawlerList) {
        this.submitCrawlerList = submitCrawlerList;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
