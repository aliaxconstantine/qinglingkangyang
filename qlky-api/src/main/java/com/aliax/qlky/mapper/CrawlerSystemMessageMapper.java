package com.aliax.qlky.mapper;

import com.aliax.qlky.entity.CrawlerSystemMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 艾莉希雅
* @description 针对表【crawler_system_message】的数据库操作Mapper
* @createDate 2025-05-15 11:03:56
* @Entity com.aliax.qlky.entity.CrawlerSystemMessage
*/
public interface CrawlerSystemMessageMapper extends BaseMapper<CrawlerSystemMessage> {

    List<CrawlerSystemMessage> selectMessageList(CrawlerSystemMessage crawlerSystemMessage);
}




