package com.aliax.qlky.entity;

import com.aliax.qlky.bean.basebean.PageBean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * (CrawlerData)实体类
 *
 * @author makejava
 * @since 2025-02-11 22:42:40
 */
@TableName("crawler_data")
public class CrawlerDataEntity extends PageBean<CrawlerDataEntity> implements Serializable {
    private static final long serialVersionUID = 487704216115703786L;
    /**
     * 数据ID，主键，自动递增
     */
    @TableId(value ="data_id", type = IdType.AUTO)
    private Integer dataId;
    /**
     * 关联爬虫ID，表示该数据属于哪个爬虫
     */
    private Integer crawlerId;
    /**
     * 关联任务ID，表示该数据属于哪个任务
     */
    private Integer taskId;
    /**
     * 存储抓取的数据，格式化存储为JSON格式
     */
    private String data;
    /**
     * 创建时间，默认当前时间
     */
    private Date createdAt;
    /**
     * 更新时间，自动更新
     */
    private Date updatedAt;

    /**
     * 字段ID
     */
    @TableField(value = "fieldid")
    private Integer fieldId;

    /**
     * 组ID
     */
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

