package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.config.cantants.SystemConstants;
import com.aliax.qlky.entity.CrawlerDataEntity;
import com.aliax.qlky.entity.CrawlerEntity;
import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.aliax.qlky.service.CrawlerDataService;
import com.aliax.qlky.service.CrawlerService;
import com.aliax.qlky.service.CrawlerTaskService;
import com.aliax.qlky.service.impl.CrawlerSchedulerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫任务接口
 */
@RestController
public class CrawlerTaskController {

    @Autowired
    private CrawlerTaskService crawlerTaskService;
    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private CrawlerSchedulerService crawlerSchedulerService;

    @Autowired
    private CrawlerDataService crawlerDataService;

    /**
     * 获取爬虫任务
     *
     * @param crawlerTaskEntity
     * @return
     */
    @RequestMapping("/getCrawlerTask")
    public HttpResult getCrawlerTask(@RequestBody CrawlerTaskEntity crawlerTaskEntity) {
        QueryWrapper<CrawlerTaskEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("crawler_id", crawlerTaskEntity.getCrawlerId());
        CrawlerTaskEntity crawlerTask = crawlerTaskService.getOne(objectQueryWrapper);
        return HttpResult.success(crawlerTask);
    }

    //获取爬虫列表
    @RequestMapping("/getCrawlerTaskList")
    public HttpResult getCrawlerTaskList(@RequestBody CrawlerTaskEntity crawlerTaskEntity) {
        Page<CrawlerTaskEntity> crawlerTaskEntityPage = crawlerTaskService.selectTaskList(crawlerTaskEntity);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("crawlerId", crawlerService.selectWithCrawlerName(new CrawlerEntity()).getRecords());
        retMap.put("list", crawlerTaskEntityPage.getRecords());
        return HttpResult.success(retMap, crawlerTaskEntityPage.getTotal());
    }


    /**
     * 创建爬虫任务
     *
     * @param crawlerTaskEntity
     * @return
     */
    @RequestMapping("/saveCrawlerTask")
    public HttpResult createCrawlerTask(@RequestBody CrawlerTaskEntity crawlerTaskEntity) {
        crawlerTaskEntity.setTaskStatus(SystemConstants.INACTIVE_STATE);
        boolean save = crawlerTaskService.saveOrUpdate(crawlerTaskEntity);
        return save ? HttpResult.success("创建成功！") : HttpResult.fail("创建失败！");
    }

    /**
     * 删除爬虫任务
     *
     * @param crawlerTaskEntity
     * @return
     */
    @RequestMapping("/deleteCrawlerTask")
    public HttpResult deleteCrawlerTask(@RequestBody CrawlerTaskEntity crawlerTaskEntity) {
        QueryWrapper<CrawlerTaskEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("task_id", crawlerTaskEntity.getTaskId());
        boolean remove = crawlerTaskService.remove(objectQueryWrapper);
        return remove ? HttpResult.success("删除成功！") : HttpResult.fail("删除失败！");
    }


    @RequestMapping("/executeCrawlerTask")
    public HttpResult executeCrawlerTask(@RequestBody CrawlerTaskEntity crawlerTaskEntity) {
        boolean update = false;
        CrawlerTaskEntity crawlerTask = crawlerTaskService.getById(crawlerTaskEntity.getTaskId());
        //获取爬虫
        if (crawlerTask == null) {
            return HttpResult.fail("该任务不存在！");
        }
        CrawlerEntity crawlerEntity = crawlerService.getById(crawlerTask.getCrawlerId());
        if (crawlerEntity == null || crawlerEntity.getCrawlerProgramPath() == null) {
           return HttpResult.fail("请先上传脚本或者创建爬虫");
        }
        //如果该任务正在执行
        if (crawlerTask.getTaskStatus() != null && crawlerTask.getTaskStatus().equals(SystemConstants.ACTIVE_STATE)) {
            return HttpResult.fail("该任务正在执行，请稍后再试！");
        }
        try {
            //表示正在执行
            crawlerTask.setTaskStatus(SystemConstants.ACTIVE_STATE);
            update = crawlerTaskService.updateById(crawlerTask);
            //将爬虫挨个执行
            crawlerService.executeCrawler(crawlerEntity);
            return HttpResult.success("执行成功！");
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        } finally {
            if (update) {
                crawlerTask.setTaskStatus(SystemConstants.INACTIVE_STATE);
                crawlerTaskService.updateById(crawlerTask);
            }
        }
    }


    @RequestMapping("/cancelCrawlerTask")
    public HttpResult cancelCrawlerTask(@RequestBody CrawlerTaskEntity crawlerTaskEntity) {
        try {
            //从控制台停止正在运行的脚本
            CrawlerTaskEntity crawlerTask = crawlerTaskService.getById(crawlerTaskEntity.getTaskId());
            crawlerSchedulerService.stopTask(crawlerTaskEntity.getTaskId());
            crawlerService.killPythonByPathEnhanced(crawlerTask.getCrawlerId());
            return HttpResult.success("停止任务成功");
        }catch (Exception e){
            return HttpResult.fail(e.getMessage());
        }
    }

    @RequestMapping("/deleteCrawlerData")
    public HttpResult deleteCrawlerData(@RequestBody CrawlerDataEntity crawlerDataEntity) {
        QueryWrapper<CrawlerDataEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("task_id",  crawlerDataEntity.getTaskId());
        boolean remove = crawlerDataService.remove(objectQueryWrapper);
        return remove ? HttpResult.success("删除该任务下数据集成功！") : HttpResult.fail("删除该任务下数据集失败！");
    }
}
