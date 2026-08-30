package com.aliax.qlky.mapper;

import com.aliax.qlky.entity.CrawlerSystemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 艾莉希雅
*/
@Mapper
public interface CrawlerSystemMapper extends BaseMapper<CrawlerSystemEntity> {

    List<CrawlerSystemEntity> selectWithCrawlerName(CrawlerSystemEntity crawlerSystemEntity);
}




