package com.aliax.qlky.service.impl;

import com.aliax.qlky.bean.baseenum.DbFieldType;
import com.aliax.qlky.bean.qlky.CrawlerQueryBean;
import com.aliax.qlky.entity.CrawlerDataEntity;
import com.aliax.qlky.entity.CrawlerDataFieldEntity;
import com.aliax.qlky.mapper.CrawlerDataMapper;
import com.aliax.qlky.service.CrawlerDataFieldService;
import com.aliax.qlky.service.CrawlerDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 艾莉希雅
 * @description 针对表【crawler_data】的数据库操作Service实现
 * @createDate 2025-02-11 22:40:59
 */
@Service
public class CrawlerDataServiceImpl extends ServiceImpl<CrawlerDataMapper, CrawlerDataEntity>
        implements CrawlerDataService {
    @Autowired
    private CrawlerDataFieldService crawlerDataFieldService;
    @Autowired
    private CrawlerDataMapper crawlerDataMapper;

    @Override
    public Map<String, Object> getTableDataByCrawlerId(Integer crawlerId) {
        List<CrawlerDataFieldEntity> dataFieldByCrawlerId = crawlerDataFieldService.getDataFieldByCrawlerId(crawlerId);
        Map<String, Object> result = new HashMap<>();
        result.put("tableHead", convertColumns(dataFieldByCrawlerId));
        result.put("searchHead", new ArrayList<>());
        result.put("page", buildPageInfo());
        result.put("isLoading", false);
        result.put("tableData", new ArrayList<>());
        result.put("formFlag", 1);
        result.put("formData", new ArrayList<>());
        return result;
    }


    @Override
    public Page<String> getMapByGroupId(@NotNull CrawlerDataEntity crawlerDataEntity) {
        if (crawlerDataEntity.getCurrentPage() > 0) {
            crawlerDataEntity.setOffset((crawlerDataEntity.getCurrentPage() - 1) * crawlerDataEntity.getPageSize());
        }
        List<String> stringObjectMap = crawlerDataMapper.selectMapByGroupId(crawlerDataEntity);
        Page<String> objectPage = new Page<>();
        objectPage.setRecords(stringObjectMap);
        objectPage.setTotal(crawlerDataMapper.countMapByGroupId(crawlerDataEntity));
        return objectPage;
    }


    public Map<String, Object> getQueryMap(CrawlerQueryBean crawlerQueryBean, boolean isCount) {
        Map<String, Object> queryMap = new HashMap<>();
        if (crawlerQueryBean.getQueryList() != null) {
            if (!isCount) {
                queryMap.putAll(crawlerQueryBean.getQueryList());
            } else {
                if (crawlerQueryBean.getQueryMoreCountList() != null) {
                    queryMap.put("query", crawlerQueryBean.getQueryMoreCountList());
                }
            }
        }
        if (crawlerQueryBean.getQueryEndTime() != null && crawlerQueryBean.getQueryStartTime() != null) {
            queryMap.put("start", crawlerQueryBean.getQueryStartTime().getTime());
            queryMap.put("end", crawlerQueryBean.getQueryEndTime().getTime());
        }
        queryMap.put("crawlerId", crawlerQueryBean.getCrawlerId());
        queryMap.put("len", (crawlerQueryBean.getCurrentPage() - 1) * crawlerQueryBean.getPageSize());
        queryMap.put("index", crawlerQueryBean.getPageSize());
        return queryMap;
    }

    @Override
    public Page<Map<String, Object>> getCrawlerDataByCrawlerId(CrawlerQueryBean crawlerQueryBean) {
        Page<Map<String, Object>> objectPage = new Page<>();
        List<CrawlerDataFieldEntity> fields = crawlerDataFieldService.getDataFieldByCrawlerId(crawlerQueryBean.getCrawlerId());
        Map<Integer, String> fieldNames = fields.stream().collect(Collectors.toMap(
                CrawlerDataFieldEntity::getFieldId,
                CrawlerDataFieldEntity::getFieldName,
                (first, ignored) -> first
        ));
        LambdaQueryWrapper<CrawlerDataEntity> query = new LambdaQueryWrapper<CrawlerDataEntity>()
                .eq(crawlerQueryBean.getCrawlerId() != null, CrawlerDataEntity::getCrawlerId, crawlerQueryBean.getCrawlerId())
                .ge(crawlerQueryBean.getQueryStartTime() != null, CrawlerDataEntity::getCreatedAt, crawlerQueryBean.getQueryStartTime())
                .lt(crawlerQueryBean.getQueryEndTime() != null, CrawlerDataEntity::getCreatedAt, crawlerQueryBean.getQueryEndTime())
                .orderByDesc(CrawlerDataEntity::getCreatedAt);
        LinkedHashMap<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        for (CrawlerDataEntity row : list(query)) {
            Map<String, Object> record = grouped.computeIfAbsent(row.getGroupId(), groupId -> {
                Map<String, Object> value = new LinkedHashMap<>();
                value.put("group_id", groupId);
                return value;
            });
            String fieldName = fieldNames.get(row.getFieldId());
            if (fieldName != null) {
                record.put(fieldName, row.getData());
            }
        }
        List<Map<String, Object>> filtered = grouped.values().stream()
                .filter(record -> matchesQuery(record, crawlerQueryBean.getQueryList()))
                .collect(Collectors.toList());
        int pageSize = Math.max(1, crawlerQueryBean.getPageSize());
        int currentPage = Math.max(1, crawlerQueryBean.getCurrentPage());
        int start = Math.min((currentPage - 1) * pageSize, filtered.size());
        int end = Math.min(start + pageSize, filtered.size());
        objectPage.setRecords(filtered.subList(start, end));
        objectPage.setTotal(filtered.size());
        objectPage.setCurrent(currentPage);
        objectPage.setSize(pageSize);
        return objectPage;
    }

    @Override
    public Map<String, Object> getCrawlerCountByCrawlerId(CrawlerQueryBean crawlerQueryBean) {
        Map<String, Object> queryMap = getQueryMap(crawlerQueryBean, true);
        return crawlerDataMapper.countMoreCrawlerDataByQueryMap(queryMap);
    }

    @Override
    public List<String> existsByAllData(List<String> submitCrawlerList) {
        if (submitCrawlerList.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> existing = list(new LambdaQueryWrapper<CrawlerDataEntity>()
                .in(CrawlerDataEntity::getGroupId, submitCrawlerList)
                .select(CrawlerDataEntity::getGroupId))
                .stream()
                .map(CrawlerDataEntity::getGroupId)
                .collect(Collectors.toSet());
        return submitCrawlerList.stream().filter(groupId -> !existing.contains(groupId)).collect(Collectors.toList());
    }

    private boolean matchesQuery(Map<String, Object> record, Map<String, Object> queryList) {
        if (queryList == null || queryList.isEmpty()) {
            return true;
        }
        return queryList.entrySet().stream().allMatch(entry -> Objects.equals(
                String.valueOf(record.get(entry.getKey())),
                String.valueOf(entry.getValue())
        ));
    }

    private List<Map<String, Object>> convertColumns(List<CrawlerDataFieldEntity> columns) {
        List<Map<String, Object>> collect = columns.stream()
                .sorted(Comparator.comparingInt(CrawlerDataFieldEntity::getSortid))
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("label", c.getFieldDescription());
                    map.put("prop", c.getFieldName());
                    DbFieldType.getById(Integer.parseInt(c.getFieldType())).ifPresent(s -> map.put("datatype", s.getVueType()));
                    map.put("tableShow", true);
                    map.put("formShow", true);
                    map.put("required", true);
                    return map;
                }).collect(Collectors.toList());
        //添加修改删除按钮
        collect.add(new HashMap<>() {{
            put("label", "修改");
            put("prop", "update");
            put("datatype", "button");
            put("tableShow", true);
            put("formShow", false);
            put("required", false);
        }});
        collect.add(new HashMap<>() {{
            put("label", "删除");
            put("prop", "delete");
            put("datatype", "button");
            put("tableShow", true);
            put("formShow", false);
            put("required", false);
        }});
        return collect;
    }


    private Map<String, Object> buildPageInfo() {
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("currentPage", 0);
        pageInfo.put("pageSize", 20);
        pageInfo.put("total", 0); // 需要实际查询后设置
        return pageInfo;
    }
}




