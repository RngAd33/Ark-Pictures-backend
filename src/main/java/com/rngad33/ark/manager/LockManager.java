package com.rngad33.ark.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 通用锁管理器
 */
@Component
@Slf4j
public class LockManager {

    @Resource
    private RedissonClient redissonClient;


    /**
     * 尝试获取锁
     *
     * @param lockKey 锁的 key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean success = lock.tryLock(waitTime, leaseTime, unit);
            if (success) {
                log.info("获取 Redisson 锁成功，lockKey: {}", lockKey);
            } else {
                log.info("获取 Redisson 锁失败，lockKey: {}", lockKey);
            }
            return success;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取 Redisson 锁被中断", e);
            return false;
        } catch (Exception e) {
            log.error("获取 Redisson 锁异常", e);
            return false;
        }
    }

    /**
     * 获取锁（阻塞直到获取成功）
     */
    public void lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        log.info("获取 Redisson 锁成功，lockKey: {}", lockKey);
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("释放 Redisson 锁成功，lockKey: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放 Redisson 锁异常", lockKey, e);
        }
    }

}