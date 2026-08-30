package com.aliax.qlky.entity;

import com.aliax.qlky.bean.basebean.PageBean;

import java.util.Date;
import java.io.Serializable;

/**
 * (CrawlerSystemMessage)实体类
 *
 * @author makejava
 * @since 2025-05-15 11:05:36
 */
public class CrawlerSystemMessage extends PageBean<CrawlerSystemMessage> implements Serializable {
    private static final long serialVersionUID = 698222511578722387L;
    
    private Integer messageid;
    
    private String message;
    
    private Date createtime;
    
    private Date updatetime;


    public Integer getMessageid() {
        return messageid;
    }

    public void setMessageid(Integer messageid) {
        this.messageid = messageid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

}

