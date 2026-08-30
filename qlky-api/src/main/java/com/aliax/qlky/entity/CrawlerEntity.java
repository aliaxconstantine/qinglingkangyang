package com.aliax.qlky.entity;

import com.aliax.qlky.bean.basebean.PageBean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * (Crawler)实体类
 *
 * @author makejava
 * @since 2025-02-11 22:41:50
 */
@TableName("crawler")
public class CrawlerEntity extends PageBean<CrawlerEntity> implements Serializable {
    private static final long serialVersionUID = -73645421904740086L;
    /**
     * 爬虫ID，主键，自动递增
     */
    @TableId(value = "crawler_id", type = IdType.AUTO)
    private Integer crawlerId;
    /**
     * 爬虫名称，标识爬虫的名字
     */
    private String crawlerName;
    /**
     * 爬虫描述，记录爬虫的具体功能和用途
     */
    private String crawlerDescription;
    /**
     * 爬虫程序路径，记录爬虫程序的存储位置
     */
    private String crawlerProgramPath;
    /**
     * 爬虫开始时间，表示爬虫的启动时间
     */
    private Date startTime;
    /**
     * 爬虫结束时间，表示爬虫的结束时间
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

    // 新增菜单相关字段
    @TableField(exist = false)
    private String menuIcon;       // 菜单图标
    @TableField(exist = false)
    private Integer menuSort;      // 菜单排序
    @TableField(exist = false)
    private Integer menuVisible;   // 是否显示在菜单(0:隐藏,1:显示)

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
    }

    public Integer getMenuVisible() {
        return menuVisible;
    }

    public void setMenuVisible(Integer menuVisible) {
        this.menuVisible = menuVisible;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public String getCrawlerName() {
        return crawlerName;
    }

    public void setCrawlerName(String crawlerName) {
        this.crawlerName = crawlerName;
    }

    public String getCrawlerDescription() {
        return crawlerDescription;
    }

    public void setCrawlerDescription(String crawlerDescription) {
        this.crawlerDescription = crawlerDescription;
    }

    public String getCrawlerProgramPath() {
        return crawlerProgramPath;
    }

    public void setCrawlerProgramPath(String crawlerProgramPath) {
        this.crawlerProgramPath = crawlerProgramPath;
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

