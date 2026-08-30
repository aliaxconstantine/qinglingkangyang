package com.aliax.qlky.init;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author 艾莉希雅
 */
@MapperScan("com.aliax.*.mapper")
@ComponentScan(basePackages = {"com.*", "com.aliax.qlky.config"})
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RestControllerAdvice
@EnableScheduling
@EnableAsync
public class QLKYInitApplication {
    public static void main(String[] args) {
        SpringApplication.run(QLKYInitApplication.class, args);
    }
}
