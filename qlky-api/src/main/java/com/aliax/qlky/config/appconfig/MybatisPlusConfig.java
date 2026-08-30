package com.aliax.qlky.config.appconfig;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(@Autowired @Qualifier("dataSource") DataSource dataSource) throws Exception {
        // 创建 MybatisPlus 拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页插件，配置 DB 类型为 MySQL
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 配置 Mybatis
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        // 添加 MybatisPlus 拦截器到 Mybatis 配置中
        mybatisConfiguration.addInterceptor(mybatisPlusInterceptor);
        // 开启缓存
        mybatisConfiguration.setCacheEnabled(true);
        // 创建 MybatisSqlSessionFactoryBean 实例
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        // 设置数据源
        sqlSessionFactoryBean.setDataSource(dataSource);
        // 设置 Mybatis 配置
        sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
        // 设置拦截器（这里需要传入拦截器数组）
        sqlSessionFactoryBean.setPlugins(new MybatisPlusInterceptor[] { mybatisPlusInterceptor });
        // 返回配置好的 SqlSessionFactory
        return sqlSessionFactoryBean;
    }


}
