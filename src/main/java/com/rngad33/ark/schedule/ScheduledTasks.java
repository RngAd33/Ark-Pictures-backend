package com.rngad33.ark.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 */
@Component
public class ScheduledTasks {

    /**
     * 同步点赞信息到数据库
     */
    @Scheduled(fixedRate = 500000)
    public void doSynchronize(){

    }

}