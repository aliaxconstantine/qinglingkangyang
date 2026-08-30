package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.bean.qlky.CrawlerQueryBean;
import com.aliax.qlky.bean.qlky.SubmitCrawler;
import com.aliax.qlky.entity.CrawlerDataEntity;
import com.aliax.qlky.entity.CrawlerDataFieldEntity;
import com.aliax.qlky.service.CrawlerDataFieldService;
import com.aliax.qlky.service.CrawlerDataService;
import com.aliax.qlky.service.CrawlerService;
import com.aliax.qlky.utils.DataSigner;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 爬虫数据管理接口
 */
@RestController
public class CrawlerDataManagerController {
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerDataFieldService crawlerDataFieldService;
    @Autowired
    private CrawlerDataService crawlerDataService;

    @RequestMapping("/submitCrawlerData")
    public HttpResult submitCrawlerData(@RequestBody String putInStr) {
        SubmitCrawler submitCrawler = JSONObject.parseObject(putInStr, SubmitCrawler.class);
        Integer crawlerId = submitCrawler.getCrawlerId();
        //先获取字段
        List<CrawlerDataFieldEntity> dataFieldByCrawlerId = crawlerDataFieldService.getDataFieldByCrawlerId(crawlerId);
        CrawlerQueryBean crawlerQueryBean = new CrawlerQueryBean();
        crawlerQueryBean.setCrawlerId(crawlerId);
        List<Map<String, Object>> exists = submitCrawler.getSubmitCrawlerList();
        List<String> existKeys = exists.stream()
                .map(item -> {
                    // 生成签名并存入map
                    String groupId = DataSigner.generateSignature(
                            item,
                            "group_id", "create_time", "task_id"
                    );
                    item.put("group_id", groupId);
                    return groupId;
                })
                .collect(Collectors.toList());
        List<String> selectKeys = crawlerDataService.existsByAllData(existKeys);
        Set<String> existingSet = new HashSet<>(selectKeys);
        List<Map<String, Object>> filteredList = exists.stream()
                .filter(map -> {
                    Object id = map.get("group_id");
                    return id instanceof String
                            && existingSet.contains(id);
                })
                .toList();
        for (Map<String, Object> stringObjectEntry : filteredList) {
            for (Map.Entry<String, Object> objectEntry : stringObjectEntry.entrySet()) {
                Optional<CrawlerDataFieldEntity> first = dataFieldByCrawlerId.stream().filter(item -> item.getFieldName().equals(objectEntry.getKey())).findFirst();
                if (first.isPresent()) {
                    CrawlerDataFieldEntity crawlerDataFieldEntity = first.get();
                    CrawlerDataEntity crawlerDataEntity = new CrawlerDataEntity();
                    crawlerDataEntity.setCrawlerId(crawlerId);
                    crawlerDataEntity.setTaskId(submitCrawler.getTaskId());
                    crawlerDataEntity.setData(String.valueOf(objectEntry.getValue()));
                    crawlerDataEntity.setFieldId(crawlerDataFieldEntity.getFieldId());
                    crawlerDataEntity.setGroupId((String) stringObjectEntry.get("group_id"));
                    crawlerDataService.save(crawlerDataEntity);
                }
            }
        }
        return HttpResult.success("上传成功！");
    }

    @RequestMapping("/getCrawlerData")
    public HttpResult getCrawlerData(@RequestBody CrawlerDataEntity crawlerDataEntity) {
        return HttpResult.success(crawlerService.getCrawlerDataByCrawlerId(crawlerDataEntity));
    }


    //获取分页数据
    @RequestMapping("/pageData")
    public HttpResult getPageCrawlerData(@RequestBody CrawlerQueryBean crawlerQueryBean) {
        return HttpResult.success(crawlerDataService.getCrawlerDataByCrawlerId(crawlerQueryBean));
    }

    //获取统计数据qlky
    @RequestMapping("/getCountData")
    public HttpResult getCountData(@RequestBody CrawlerQueryBean crawlerQueryBean) {
        return HttpResult.success(crawlerDataService.getCrawlerCountByCrawlerId(crawlerQueryBean));
    }

    @RequestMapping("/updateOrSaveData")
    public HttpResult updateOrSaveData(@RequestBody Map<String, Object> saveData) {
        Integer crawlerId = (Integer) saveData.get("crawlerId");
        String groupId = (String) saveData.get("group_id");
        //生成组ID
        String newGroupId = crawlerId + new Date().getTime() + "-" + 1;
        saveData.remove("crawlerId");
        saveData.remove("groupId");
        for (Map.Entry<String, Object> stringObjectEntry : saveData.entrySet()) {
            QueryWrapper<CrawlerDataFieldEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("crawler_id", crawlerId);
            queryWrapper.eq("field_name", stringObjectEntry.getKey());
            CrawlerDataFieldEntity fieldInfo = crawlerDataFieldService.getOne(queryWrapper);
            if (fieldInfo != null) {
                CrawlerDataEntity crawlerDataEntity = new CrawlerDataEntity();
                crawlerDataEntity.setCrawlerId(crawlerId);
                crawlerDataEntity.setFieldId(fieldInfo.getFieldId());
                crawlerDataEntity.setData(String.valueOf(stringObjectEntry.getValue()));
                if (groupId == null) {
                    crawlerDataEntity.setGroupId(newGroupId);
                    //后台添加
                    crawlerDataEntity.setTaskId(0);
                    crawlerDataService.save(crawlerDataEntity);
                } else {
                    //根据名称和爬虫ID查找对应的名称
                    QueryWrapper<CrawlerDataEntity> dataEntityQueryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("group_id", groupId);
                    queryWrapper.eq("fieldid", crawlerId);
                    crawlerDataService.update(crawlerDataEntity, dataEntityQueryWrapper);
                }
            }
        }
        return HttpResult.success("保存成功！");
    }

    @RequestMapping("/deleteData")
    public HttpResult deleteData(@RequestBody CrawlerDataEntity crawlerDataEntity) {
        QueryWrapper<CrawlerDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", crawlerDataEntity.getGroupId());
        queryWrapper.eq("crawler_id", crawlerDataEntity.getCrawlerId());
        crawlerDataService.remove(queryWrapper);
        return HttpResult.success("删除成功！");
    }

}
