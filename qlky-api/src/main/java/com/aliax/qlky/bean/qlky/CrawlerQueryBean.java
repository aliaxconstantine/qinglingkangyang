package com.aliax.qlky.bean.qlky;

import com.aliax.qlky.bean.basebean.PageBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CrawlerQueryBean extends PageBean<Map<String,Object>> {

    private Integer crawlerId;

    private Map<String,Object> queryList;

    private List<Map<String,Object>> queryMoreCountList;

    private Date queryStartTime;

    private Date queryEndTime;

    public List<Map<String, Object>> getQueryMoreCountList() {
        return queryMoreCountList;
    }

    public void setQueryMoreCountList(List<Map<String, Object>> queryMoreCountList) {
        this.queryMoreCountList = queryMoreCountList;
    }

    public Date getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(Date queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public Date getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(Date queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public Integer getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(Integer crawlerId) {
        this.crawlerId = crawlerId;
    }

    public Map<String, Object> getQueryList() {
        return queryList;
    }

    public void setQueryList(Map<String, Object> queryList) {
        this.queryList = queryList;
    }
}
