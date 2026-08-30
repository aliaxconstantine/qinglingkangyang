package com.aliax.qlky.entity;

import com.aliax.qlky.bean.basebean.PageBean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * (CrawlerDataField)实体类
 *
 * @author makejava
 * @since 2025-02-11 22:42:56
 */
@TableName("crawler_data_field")
public class CrawlerDataFieldEntity extends PageBean<CrawlerDataFieldEntity> implements Serializable {
    private static final long serialVersionUID = -28409213528631696L;
    /**
     * 字段ID，主键，自动递增
     */
    @TableId(value ="field_id", type = IdType.AUTO)
    private Integer fieldId;
    /**
     * 关联爬虫ID，表示该字段属于哪个爬虫
     */
    private Integer crawlerId;
    /**
     * 字段名称，记录该数据字段的名称
     */
    private String fieldName;
    /**
     * 字段类型，如字符串、整数等，存储字段类型
     */
    private String fieldType;
    /**
     * 字段描述，用于说明该字段的用途或内容
     */
    private String fieldDescription;
    /**
     * 创建时间，默认当前时间
     */
    private Date createdAt;
    /**
     * 更新时间，自动更新
     */
    private Date updatedAt;


    /**
     * 排序ID
     */
    private Integer sortid;

    public Integer getSortid() {
        return sortid;
    }

    public void setSortid(Integer sortid) {
        this.sortid = sortid;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
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

