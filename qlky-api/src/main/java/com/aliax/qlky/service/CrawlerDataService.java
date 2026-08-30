package com.aliax.qlky.service;

import com.aliax.qlky.bean.qlky.CrawlerQueryBean;
import com.aliax.qlky.entity.CrawlerDataEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author 艾莉希雅
* @description 针对表【crawler_data】的数据库操作Service
* @createDate 2025-02-11 22:40:59
*/
public interface CrawlerDataService extends IService<CrawlerDataEntity> {


    Map<String,Object> getTableDataByCrawlerId(Integer crawlerId);

    Page<Map<String,Object>> getCrawlerDataByCrawlerId(CrawlerQueryBean crawlerQueryBean);

    Page<String> getMapByGroupId(CrawlerDataEntity crawlerDataEntity);

    /**
     * 根据字段名称获取该爬虫id下的数据数量
     * @param crawlerQueryBean
     * @return
     */
    Map<String,Object> getCrawlerCountByCrawlerId(CrawlerQueryBean crawlerQueryBean);


    List<String> existsByAllData(List<String> submitCrawlerList);
}

