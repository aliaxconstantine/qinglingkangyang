package com.aliax.qlky.mapper;

import com.aliax.qlky.entity.CrawlerDataFieldEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 艾莉希雅
* @description 针对表【crawler_data_field】的数据库操作Mapper
* @createDate 2025-02-11 22:41:02
* @Entity com.aliax.qlky.com.aliax.qlky.entity.CrawlerDataField
*/
public interface CrawlerDataFieldMapper extends BaseMapper<CrawlerDataFieldEntity> {

    List<CrawlerDataFieldEntity> selectListByCrawlerId(CrawlerDataFieldEntity crawlerDataFieldEntity);
}




