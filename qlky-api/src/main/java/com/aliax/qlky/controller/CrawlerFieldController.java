package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.entity.CrawlerDataFieldEntity;
import com.aliax.qlky.service.CrawlerDataFieldService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlerFieldController {

    @Autowired
    private CrawlerDataFieldService crawlerDataFieldService;

    /**
     * 获取字段列表
     * @param crawlerDataFieldEntity
     * @return
     */
    @RequestMapping("/getCrawlerFieldList")
    public HttpResult getCrawlerFieldList(@RequestBody CrawlerDataFieldEntity crawlerDataFieldEntity){
        Page<CrawlerDataFieldEntity> crawlerDataFieldEntityPage = crawlerDataFieldService.selectFieldByCrawlerId(crawlerDataFieldEntity);
        QueryWrapper<CrawlerDataFieldEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("crawler_id", crawlerDataFieldEntity.getCrawlerId());
        return HttpResult.success(crawlerDataFieldEntityPage.getRecords(), crawlerDataFieldService.count(objectQueryWrapper));
    }

    /**
     * 删除字段
     * @param crawlerDataFieldEntity
     * @return
     */
    @RequestMapping("/deleteCrawlerField")
    public HttpResult deleteCrawlerField(@RequestBody CrawlerDataFieldEntity crawlerDataFieldEntity){
        QueryWrapper<CrawlerDataFieldEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("field_id", crawlerDataFieldEntity.getFieldId());
        boolean b = crawlerDataFieldService.remove(objectQueryWrapper);
        if (b){
            return HttpResult.success("删除成功！");
        }
        return HttpResult.fail("删除失败！");
    }

    /**
     * 保存字段
     * @param crawlerDataFieldEntity
     * @return
     */
    @RequestMapping("/saveCrawlerField")
    public HttpResult saveCrawlerField(@RequestBody CrawlerDataFieldEntity crawlerDataFieldEntity){
        boolean b = crawlerDataFieldService.saveOrUpdate(crawlerDataFieldEntity);
        return HttpResult.success("保存成功！");
    }

}
