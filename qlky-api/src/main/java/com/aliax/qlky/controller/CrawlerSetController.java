package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.entity.CrawlerSystemEntity;
import com.aliax.qlky.service.CrawlerSystemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlerSetController {
    @Autowired
    private CrawlerSystemService crawlerSystemService;

    @RequestMapping("/SystemList")
    public HttpResult SystemList(@RequestBody CrawlerSystemEntity crawlerSystemEntity) {
        Page<CrawlerSystemEntity> crawlerSystemEntityPage = crawlerSystemService.selectWithCrawlerName(crawlerSystemEntity);
        return HttpResult.success(crawlerSystemEntityPage.getRecords(), crawlerSystemEntityPage.getTotal());
    }

    @RequestMapping("/SystemSave")
    public HttpResult SystemSave(@RequestBody CrawlerSystemEntity crawlerSystemEntity) {
        crawlerSystemService.saveOrUpdate(crawlerSystemEntity); // 新增
        return HttpResult.success("保存成功");
    }

    @RequestMapping("/SystemDelete")
    public HttpResult SystemDelete(@RequestBody CrawlerSystemEntity crawlerSystemEntity) {
        if (crawlerSystemEntity.getSystemId() == null) {
            return HttpResult.fail("缺少参数：systemId");
        }
        QueryWrapper<CrawlerSystemEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("system_id", crawlerSystemEntity.getSystemId());
        crawlerSystemService.remove(objectQueryWrapper);
        return HttpResult.success( "删除成功");
    }


}
