package com.aliax.qlky.controller;

import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.dto.CrawlerMenuDTO;
import com.aliax.qlky.entity.CrawlerEntity;
import com.aliax.qlky.service.CrawlerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class CrawlerMenuController {

    @Autowired
    private CrawlerService crawlerService;

    /**
     * 获取所有可显示的爬虫菜单
     */
    @PostMapping("/menulist")
    public HttpResult getCrawlerMenus() {
        //获取所有爬虫
        Page<CrawlerEntity> crawlerEntityPage = crawlerService.selectWithCrawlerName(new CrawlerEntity());
        List<CrawlerEntity> records = crawlerEntityPage.getRecords();
        List<CrawlerMenuDTO> menus = new ArrayList<>();
        menus.add(createHomeMenu());
        menus.addAll(buildFixedMenus());
        menus.addAll(records.stream()
                .map(CrawlerMenuDTO::fromCrawler)
                .toList());
        return HttpResult.success(menus, (long) menus.size());
    }

    // 创建固定首页菜单
    private CrawlerMenuDTO createHomeMenu() {
        CrawlerMenuDTO homeMenu = new CrawlerMenuDTO();
        homeMenu.setPath("/home");
        homeMenu.setName("home");
        homeMenu.setComponent("/home/index"); // 对应前端组件路径
        homeMenu.setMeta(new CrawlerMenuDTO.Meta(
                "message.router.home", // 国际化键（需与前端对应）
                "",                     // 非外链
                false,                  // 不隐藏
                true,                   // 保持活跃
                true,                   // 固定标签页
                false,                  // 非iframe
                Arrays.asList("admin", "common"), // 允许的角色
                "iconfont icon-shouye",  // 图标类名,
                null
        ));
        homeMenu.setSort(1); // 确保排序第一
        return homeMenu;
    }

    private List<CrawlerMenuDTO> buildFixedMenus() {
        List<CrawlerMenuDTO> fixedMenus = new ArrayList<>();
        // 父级菜单
        CrawlerMenuDTO parentMenu = new CrawlerMenuDTO();
        parentMenu.setPath("/crawler");
        parentMenu.setName("crawlerTask");
        parentMenu.setComponent("Layout");
        parentMenu.setMeta(new CrawlerMenuDTO.Meta(
                "爬虫管理",
                "",
                false,
                true,
                false,
                false,
                Arrays.asList("admin", "common"),
                "iconfont icon-task",
                null
        ));
        parentMenu.setRedirect("/crawler/crawler-task");
        parentMenu.setSort(100); // 控制菜单顺序


        // 子菜单项
        List<CrawlerMenuDTO> children = new ArrayList<>();

        // 任务管理
        children.add(createChildMenu(
                "/crawler/crawler-task",
                "crawlerTask",
                "/crawlerTask/index",
                "任务管理",
                "iconfont icon-task",
                101
        ));
        // 设置管理
        children.add(createChildMenu(
                "/crawler/crawler-settings",
                "crawlerSettings",
                "/crawlerSettings/index",
                "设置管理",
                "iconfont icon-settings",
                103
        ));

        // 爬虫管理
        children.add(createChildMenu(
                "/crawler/crawler-view",
                "crawlerView",
                "/crawlerManagement/index",
                "爬虫管理",
                "iconfont icon-views",
                104
        ));

        parentMenu.setChildren(children);
        fixedMenus.add(parentMenu);

        return fixedMenus;
    }

    private CrawlerMenuDTO createChildMenu(String path, String name, String component,
                                           String title, String icon, Integer sort) {
        CrawlerMenuDTO menu = new CrawlerMenuDTO();
        menu.setPath(path);
        menu.setName(name);
        menu.setComponent(component);
        menu.setMeta(new CrawlerMenuDTO.Meta(
                title,
                "",
                false,
                true,
                false,
                false,
                Arrays.asList("admin", "common"),
                icon,
                null
        ));
        menu.setSort(sort);
        return menu;
    }

}