package com.aliax.qlky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.io.Serializable;

/**
 * (CrawlerWebsite)实体类
 *
 * @author makejava
 * @since 2025-02-11 22:44:02
 */
@TableName("crawler_website")
public class CrawlerWebsiteEntity implements Serializable {
    private static final long serialVersionUID = 143343983852672646L;
    /**
     * 网站ID，主键，自动递增
     */
    @TableId(value ="website_id", type = IdType.AUTO)
    private Integer websiteId;
    /**
     * 关联爬虫ID，表示该网站属于哪个爬虫
     */
    private Integer crawlerId;
    /**
     * 网站URL，存储被爬取网站的网址
     */
    private String websiteUrl;
    /**
     * 网站名称，存储该网站的名称
     */
    private String websiteName;
    /**
     * 网站描述，记录该网站的相关描述信息
     */
    private String websiteDescription;
    /**
     * 网站爬取开始时间，记录爬虫开始抓取该网站的时间
     */
    private Date startTime;
    /**
     * 网站爬取结束时间，记录爬虫完成抓取该网站的时间
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


    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteDescription() {
        return websiteDescription;
    }

    public void setWebsiteDescription(String websiteDescription) {
        this.websiteDescription = websiteDescription;
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

