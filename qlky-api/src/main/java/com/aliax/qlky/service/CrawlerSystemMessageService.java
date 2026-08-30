package com.aliax.qlky.service;

import com.aliax.qlky.entity.CrawlerSystemMessage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 艾莉希雅
* @description 针对表【crawler_system_message】的数据库操作Service
* @createDate 2025-05-15 11:03:56
*/
public interface CrawlerSystemMessageService extends IService<CrawlerSystemMessage> {

    Page<CrawlerSystemMessage> listWithPage(CrawlerSystemMessage crawlerSystemMessage);
}
