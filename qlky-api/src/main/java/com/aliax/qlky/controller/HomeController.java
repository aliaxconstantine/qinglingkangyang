package com.aliax.qlky.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aliax.qlky.bean.basebean.HttpResult;
import com.aliax.qlky.bean.basebean.LoginParamBean;
import com.aliax.qlky.bean.qlky.PCSystemBean;
import com.aliax.qlky.config.cantants.SystemConstants;
import com.aliax.qlky.entity.CrawlerSystemMessage;
import com.aliax.qlky.service.CrawlerService;
import com.aliax.qlky.service.CrawlerSystemMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerSystemMessageService crawlerSystemMessageService;
    @RequestMapping("/loginHome")
    public HttpResult doLogin(@RequestBody LoginParamBean loginParamBean) {
        if (ObjectUtil.equal(loginParamBean.getAccount(), SystemConstants.DEFAULT_LOGIN_USER_ACCOUNT) &&
                ObjectUtil.equal(loginParamBean.getPassword(), SystemConstants.DEFAULT_LOGIN_USER_PASSWORD)){
            return HttpResult.success("登录成功！");
        }
        return HttpResult.fail("请检查账号或密码是否输入正确！");
    }

    /**
     * 获取系统信息
     */
    @RequestMapping("/getSystemInfo")
    public HttpResult getSystemInfo() {
        PCSystemBean systemInfo = PCSystemBean.getSystemInfo();
        //获取最后三十条系统信息
        Page<CrawlerSystemMessage> crawlerSystemMessagePage = crawlerSystemMessageService.listWithPage(new CrawlerSystemMessage());
        systemInfo.setCrawlerSystemMessageList(crawlerSystemMessagePage.getRecords());
        // 假设从数据库或其他配置中读取自定义信息
        systemInfo.setSystemName("秦岭康养爬虫与大数据展示服务器");
        return HttpResult.success(systemInfo);
    }


}
