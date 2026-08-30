package com.aliax.qlky.service;

import com.aliax.qlky.entity.CrawlerSystemEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 艾莉希雅
* @description 针对表【crawler_system】的数据库操作Service
* @createDate 2025-02-11 22:41:06
*/
public interface CrawlerSystemService extends IService<CrawlerSystemEntity> {

    Page<CrawlerSystemEntity> selectWithCrawlerName(CrawlerSystemEntity crawlerSystemEntity);
}
