package com.aliax.qlky.utils;

import com.aliax.qlky.bean.basebean.PageBean;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageUtil {

    /**
     * 通过 PageHelper 进行分页查询，并返回自定义分页对象。
     * 该方法支持查询方法带有参数。
     *
     * @param pageBean    分页请求参数，包含了页码和每页条数。
     * @param queryMethod 查询方法，通常是 Mapper 中的查询方法。
     * @param <T>         查询返回的实体类类型
     * @return 自定义 Page 对象，包含了分页信息和查询结果
     */
    public static <T extends PageBean<T>> Page<T> getPage(T pageBean, QueryMethodWithParam<T, T> queryMethod) {
        //加入len与index
        pageBean.setLen((pageBean.getCurrentPage() - 1) * pageBean.getPageSize());
        pageBean.setIndex(pageBean.getPageSize());
        PageHelper.clearPage();
        // 使用 PageHelper 启动分页
        PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        // 执行查询，传入参数
        List<T> list = queryMethod.query(pageBean);
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        // 创建并返回自定义分页对象
        Page<T> resultPage = new Page<>();
        resultPage.setTotal(pageInfo.getTotal());
        resultPage.setRecords(list);
        return resultPage;
    }

    /**
     * 函数式接口，用于传递带参数的查询方法
     */
    @FunctionalInterface
    public interface QueryMethodWithParam<T, P> {
        List<T> query(P param);
    }
}
