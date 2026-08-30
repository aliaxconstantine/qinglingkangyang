package com.aliax.qlky.service.impl;

import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.aliax.qlky.mapper.CrawlerTaskMapper;
import com.aliax.qlky.service.CrawlerTaskService;
import com.aliax.qlky.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 艾莉希雅
* @description 针对表【crawler_task】的数据库操作Service实现
* @createDate 2025-02-11 22:41:08
*/
@Service
public class CrawlerTaskServiceImpl extends ServiceImpl<CrawlerTaskMapper, CrawlerTaskEntity>
    implements CrawlerTaskService{

    @Autowired
    private CrawlerTaskMapper crawlerTaskMapper;

    @Override
    public Page<CrawlerTaskEntity> selectTaskList(CrawlerTaskEntity crawlerTaskEntity) {
        return PageUtil.getPage(crawlerTaskEntity, (param) -> crawlerTaskMapper.selectCrawlerTaskList(param));
    }
}




