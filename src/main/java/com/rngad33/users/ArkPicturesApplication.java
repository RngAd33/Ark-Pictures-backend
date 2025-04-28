package com.rngad33.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动入口
 */
@SpringBootApplication
public class ArkPicturesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArkPicturesApplication.class, args);
        System.out.println("后端服务已启动>>>");
    }
}