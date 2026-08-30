package com.aliax.qlky.service;

import com.aliax.qlky.entity.CrawlerDataEntity;
import com.aliax.qlky.entity.CrawlerEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.Map;

/**
 * @author 艾莉希雅
 * @description 针对表【crawler】的数据库操作Service
 * @createDate 2025-02-11 22:40:34
 */
public interface CrawlerService extends IService<CrawlerEntity> {


    public Map<String,Object> getCrawlerDataByCrawlerId(CrawlerDataEntity crawlerDataEntity);


    public Page<CrawlerEntity> selectWithCrawlerName(CrawlerEntity crawlerEntity);


    void executeCrawler(CrawlerEntity crawlerEntity);

    void killPythonByPathEnhanced(Integer crawlerId) throws IOException;
}
