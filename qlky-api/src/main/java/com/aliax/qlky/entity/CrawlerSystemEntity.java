package com.aliax.qlky.entity;

import com.aliax.qlky.bean.basebean.PageBean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * (CrawlerSystem)实体类
 *
 * @author makejava
 * @since 2025-02-11 22:43:30
 */
@TableName("crawler_system")
public class CrawlerSystemEntity extends PageBean<CrawlerSystemEntity> {
    private static final long serialVersionUID = -36457798981704781L;
    /**
     * 系统配置ID，主键，自动递增
     */
    @TableId(value = "system_id", type = IdType.AUTO)
    private Integer systemId;
    /**
     * 关联爬虫ID，表示该配置属于哪个爬虫
     */
    private Integer crawlerId;

    /**
     * 爬虫名称，记录爬虫名称
     */
    @TableField(exist = false)
    private String crawlerName;

    /**
     * 代理设置，记录爬虫使用的代理地址
     */
    private String proxy;
    /**
     * User-Agent，爬虫请求头中的用户代理
     */
    private String userAgent;
    /**
     * 最大重试次数，定义爬虫在遇到错误时最大重试次数
     */
    private Integer maxRetry;
    /**
     * 超时时间（秒），定义每个请求的超时时间
     */
    private Integer timeout;
    /**
     * 配置开始时间，记录该配置的生效时间
     */
    private Date startTime;
    /**
     * 配置结束时间，记录配置的结束时间
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


    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(Integer maxRetry) {
        this.maxRetry = maxRetry;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
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

    public String getCrawlerName() {
        return crawlerName;
    }

    public void setCrawlerName(String crawlerName) {
        this.crawlerName = crawlerName;
    }
}

