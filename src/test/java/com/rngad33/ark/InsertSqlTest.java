package com.rngad33.ark;

import cn.hutool.core.date.StopWatch;
import com.rngad33.ark.model.entity.Picture;
import com.rngad33.ark.model.entity.User;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 该测试类用于向数据库批量插入数据
 */
@SpringBootTest
class InsertSqlTest {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    // 自定义线程池
    private final ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    @Test
    void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 2000;
        int j = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<User> users = new ArrayList<>();
            do {
                j++;
                User user = new User();
                user.setUserName("祈-我ら神祖と共に歩む者なり");
                user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
                user.setUserPassword("12345678");
                user.setPhone("4444");
                user.setUserStatus(0);
                user.setRole(0);
                users.add(user);
            } while (j % INSERT_NUM != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(users, 100);
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();   // 阻塞
        stopWatch.stop();   // 任务完成后才执行
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void doInsertPictures() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 3000;
        int j = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<Picture> pictures = new ArrayList<>();
            do {
                j++;
                Picture picture = new Picture();
                picture.setOriginUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
                picture.setThumbUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
                picture.setName("祈");
                picture.setIntroduction("我ら神祖と共に歩む者なり");
                picture.setCategory("CHUNITHM");
                picture.setTags("[]");
                picture.setPicSize(4444L);
                picture.setPicWidth(4444);
                picture.setPicHeight(4444);
                picture.setPicScale(1.0);
                picture.setPicFormat(".png");
                picture.setUserId(1L);
                picture.setReviewStatus(1);
                picture.setReviewMessage("pass");
                picture.setReviewerId(1L);
                pictures.add(picture);
            } while (j % INSERT_NUM != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                pictureService.saveBatch(pictures, 100);
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();   // 阻塞
        stopWatch.stop();   // 任务完成后才执行
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}