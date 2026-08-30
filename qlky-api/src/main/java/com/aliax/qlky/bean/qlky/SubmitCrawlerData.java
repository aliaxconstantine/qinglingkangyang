package com.aliax.qlky.bean.qlky;

/**
 * 爬虫程序与后端数据交互类
 */
public class SubmitCrawlerData{

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段ID
     */
    private Integer fieldId;

    /**
     * 数据
     */
    private String data;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }
}
