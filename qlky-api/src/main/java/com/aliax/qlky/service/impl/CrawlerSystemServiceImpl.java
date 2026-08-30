package com.aliax.qlky.service.impl;

import com.aliax.qlky.entity.CrawlerSystemEntity;
import com.aliax.qlky.mapper.CrawlerSystemMapper;
import com.aliax.qlky.service.CrawlerSystemService;
import com.aliax.qlky.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 艾莉希雅
 */
@Service
public class CrawlerSystemServiceImpl extends ServiceImpl<CrawlerSystemMapper, CrawlerSystemEntity> implements CrawlerSystemService {
    private final CrawlerSystemMapper crawlerSystemMapper;

    @Autowired
    public CrawlerSystemServiceImpl(CrawlerSystemMapper crawlerSystemMapper) {
        this.crawlerSystemMapper = crawlerSystemMapper;
    }

    @Override
    public Page<CrawlerSystemEntity> selectWithCrawlerName(CrawlerSystemEntity crawlerSystemEntity) {
        return PageUtil.getPage(crawlerSystemEntity, crawlerSystemMapper::selectWithCrawlerName);
    }




}




