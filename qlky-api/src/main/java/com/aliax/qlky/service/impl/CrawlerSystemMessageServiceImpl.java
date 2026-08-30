package com.aliax.qlky.service.impl;

import com.aliax.qlky.entity.CrawlerSystemMessage;
import com.aliax.qlky.mapper.CrawlerSystemMessageMapper;
import com.aliax.qlky.service.CrawlerSystemMessageService;
import com.aliax.qlky.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 艾莉希雅
* @description 针对表【crawler_system_message】的数据库操作Service实现
* @createDate 2025-05-15 11:03:56
*/
@Service
public class CrawlerSystemMessageServiceImpl extends ServiceImpl<CrawlerSystemMessageMapper, CrawlerSystemMessage>
    implements CrawlerSystemMessageService{
    @Autowired
    private CrawlerSystemMessageMapper crawlerSystemMessageMapper;
    @Override
    public Page<CrawlerSystemMessage> listWithPage(CrawlerSystemMessage crawlerSystemMessage) {
        return  PageUtil.getPage(crawlerSystemMessage,(param)-> crawlerSystemMessageMapper.selectMessageList(param));
    }
}




