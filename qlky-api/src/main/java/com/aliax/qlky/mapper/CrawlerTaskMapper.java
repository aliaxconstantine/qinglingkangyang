package com.aliax.qlky.mapper;

import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 艾莉希雅
* @description 针对表【crawler_task】的数据库操作Mapper
* @createDate 2025-02-11 22:41:08
* @Entity com.aliax.qlky.entity.CrawlerTask
*/
public interface CrawlerTaskMapper extends BaseMapper<CrawlerTaskEntity> {

    List<CrawlerTaskEntity> selectCrawlerTaskList(CrawlerTaskEntity crawlerTaskEntity);
}




