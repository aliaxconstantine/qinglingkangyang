package com.aliax.qlky.init;

import com.aliax.qlky.config.cantants.SystemConstants;
import com.aliax.qlky.entity.CrawlerTaskEntity;
import com.aliax.qlky.service.CrawlerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class SystemInitializer implements CommandLineRunner {

    @Autowired
    private CrawlerTaskService crawlerTaskService;
    @Override
    public void run(String... args) throws Exception {
        checkTaskConfig();
    }

    private void checkTaskConfig() {
        List<CrawlerTaskEntity> list = crawlerTaskService.list();
        for (CrawlerTaskEntity crawlerTaskEntity : list) {
            if (crawlerTaskEntity.getTaskStatus() != null && crawlerTaskEntity.getTaskStatus().equals(SystemConstants.ACTIVE_STATE)) {
                crawlerTaskEntity.setTaskStatus(SystemConstants.INACTIVE_STATE);
                crawlerTaskService.saveOrUpdate(crawlerTaskEntity);
            }
        }
    }


}