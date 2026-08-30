package com.aliax.qlky.service;

import com.aliax.qlky.entity.CrawlerDataFieldEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 艾莉希雅
* @description 针对表【crawler_data_field】的数据库操作Service
* @createDate 2025-02-11 22:41:02
*/
public interface CrawlerDataFieldService extends IService<CrawlerDataFieldEntity> {

    /**
     * 根据爬虫id获取所有字段
     *
     */
    List<CrawlerDataFieldEntity> getDataFieldByCrawlerId(Integer crawlerId);

    /**
     * 获取分页数据
     * @param crawlerDataFieldEntity
     * @return
     */
    Page<CrawlerDataFieldEntity> selectFieldByCrawlerId(CrawlerDataFieldEntity crawlerDataFieldEntity);
}
