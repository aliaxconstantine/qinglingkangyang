package com.aliax.qlky.dto;

import com.aliax.qlky.entity.CrawlerEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class CrawlerMenuDTO {
    private String path;
    private String name;
    private String component;
    private String redirect;
    private Meta meta;
    private Integer sort;
    private List<CrawlerMenuDTO> children;

    @Data
    @AllArgsConstructor
    public static class Meta {
        // 以下是核心字段及作用说明
        private String title;        // 菜单标题（显示名称）
        private String isLink;       // 是否外链（如果是外链，需填写完整URL）
        private Boolean isHide;      // 是否隐藏菜单（true=隐藏，false=显示）
        private Boolean isKeepAlive; // 是否缓存组件（true=开启KeepAlive）
        private Boolean isAffix;     // 是否固定标签页（不可关闭）
        private Boolean isIframe;    // 是否iframe嵌套模式
        private List<String> roles;  // 允许访问的角色列表
        private String icon;         // 菜单图标类名（与前端图标库对应）
        private Integer crawlerId; //  爬虫ID
    }


    public static CrawlerMenuDTO fromCrawler(CrawlerEntity crawler) {
        CrawlerMenuDTO dto = new CrawlerMenuDTO();
        dto.setPath("/crawler/view/" + crawler.getCrawlerId());
        dto.setName("Crawler_" + crawler.getCrawlerId());
        dto.setComponent("/dataManagement/index");
        dto.setMeta(new Meta(
                crawler.getCrawlerName(),
                "",
                false,
                true,
                false,
                false,
                Arrays.asList("admin", "common"),
                "iconfont icon-spider",
                crawler.getCrawlerId()
        ));
        dto.setSort(200); // 动态菜单项排序在固定菜单之后
        return dto;
    }
}