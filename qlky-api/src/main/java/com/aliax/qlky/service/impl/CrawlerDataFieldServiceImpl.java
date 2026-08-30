package com.aliax.qlky.service.impl;

import com.aliax.qlky.entity.CrawlerDataFieldEntity;
import com.aliax.qlky.mapper.CrawlerDataFieldMapper;
import com.aliax.qlky.service.CrawlerDataFieldService;
import com.aliax.qlky.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 艾莉希雅
* @description 针对表【crawler_data_field】的数据库操作Service实现
* @createDate 2025-02-11 22:41:02
*/
@Service
public class CrawlerDataFieldServiceImpl extends ServiceImpl<CrawlerDataFieldMapper, CrawlerDataFieldEntity>
    implements CrawlerDataFieldService{

    @Autowired
    private CrawlerDataFieldMapper crawlerDataFieldMapper;

    @Override
    public List<CrawlerDataFieldEntity> getDataFieldByCrawlerId(Integer crawlerId) {
        return query().eq("crawler_id", crawlerId).list();
    }

    @Override
    public Page<CrawlerDataFieldEntity> selectFieldByCrawlerId(CrawlerDataFieldEntity crawlerDataFieldEntity) {
        return PageUtil.getPage(crawlerDataFieldEntity, (param)-> crawlerDataFieldMapper.selectListByCrawlerId(param));
    }
}




