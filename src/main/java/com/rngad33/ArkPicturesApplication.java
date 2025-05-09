package com.rngad33;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动入口
 */
@SpringBootApplication
@MapperScan("com.rngad33.web.mapper")
public class ArkPicturesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArkPicturesApplication.class, args);
        System.out.println("后端服务已启动>>>");
    }
}