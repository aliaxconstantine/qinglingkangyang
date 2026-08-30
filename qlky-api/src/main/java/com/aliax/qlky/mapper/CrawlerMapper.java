package com.aliax.qlky.mapper;

import com.aliax.qlky.entity.CrawlerEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 艾莉希雅
* @description 针对表【crawler】的数据库操作Mapper
* @createDate 2025-02-11 22:40:34
* @Entity com.aliax.qlky.entity.Crawler
*/
public interface CrawlerMapper extends BaseMapper<CrawlerEntity> {

    List<CrawlerEntity> selectCrawlerList(Object crawlerEntity);

}




