package com.aliax.qlky.mapper;

import com.aliax.qlky.entity.CrawlerDataEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* @author 艾莉希雅
* @description 针对表【crawler_data】的数据库操作Mapper
* @createDate 2025-02-11 22:40:59
* @Entity com.aliax.qlky.entity.CrawlerData
*/
@Mapper
public interface CrawlerDataMapper extends BaseMapper<CrawlerDataEntity> {

    List<String> selectMapByGroupId(CrawlerDataEntity crawlerDataEntity);

    int countMapByGroupId(CrawlerDataEntity crawlerDataEntity);

    List<Map<String, String>> selectCrawlerDataByQueryMap(Map<String,Object> queryMap);

    int countCrawlerDataByQueryMap(Map<String,Object> queryMap);

    Map<String,Object> countMoreCrawlerDataByQueryMap(Map<String,Object> queryMap);

    List<String> findNonExistingGroupIds(List<String> inputList);
}





