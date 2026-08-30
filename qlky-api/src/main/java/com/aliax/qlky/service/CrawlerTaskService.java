package com.aliax.qlky.service;

import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 艾莉希雅
* @description 针对表【crawler_task】的数据库操作Service
* @createDate 2025-02-11 22:41:08
*/
public interface CrawlerTaskService extends IService<CrawlerTaskEntity> {

    Page<CrawlerTaskEntity> selectTaskList(CrawlerTaskEntity crawlerTaskEntity);
}
