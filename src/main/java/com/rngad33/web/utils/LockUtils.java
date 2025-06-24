package com.rngad33.web.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁工具类（提供各种预置锁）
 *
 * @author RngAd33
 */
public class LockUtils {

    /**
     * 全局锁
     */
    private static final Object GLOBAL_LOCK = new Object();

    /**
     * 细粒度锁
     */
    private static final Map<String, Object> KEY_LOCK = new ConcurrentHashMap<>();

    /**
     * 读写锁
     */
    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();

    /**
     * 可重入锁
     */
    private static final Lock REENTRANT_LOCK = new ReentrantLock();

    /**
     * 基于 ThreadLocal 的线程局部锁
     */
    private static final ThreadLocal<Object> THREAD_LOCAL_LOCK = ThreadLocal.withInitial(Object::new);

    /**
     * 全局锁用法：
     *      synchronized (LockUtils.getGlobalLock()) {}
     */
    public static Object getGlobalLock() {
        return GLOBAL_LOCK;
    }

    /**
     * 细粒度锁用法：
     *      synchronized (LockUtils.getKeyLock(key))) {}
     */
    public static Object getKeyLock(String key) {
        return KEY_LOCK.computeIfAbsent(key, k -> new Object());
    }

    /**
     * 读写锁用法：
     *     读锁：
     *       LockUtils.getReadWriteLock().readLock().lock();
     *       try {
     *           // 读操作
     *       } finally {
     *           LockUtils.getReadWriteLock().readLock().unlock();
     *       }
     *     写锁：
     *       LockUtils.getReadWriteLock().writeLock().lock();
     *       try {
     *           // 写操作
     *       } finally {
     *           LockUtils.getReadWriteLock().writeLock().unlock();
     *       }
     */
    public static ReadWriteLock getRwLock() {
        return RW_LOCK;
    }

    /**
     * 可重入锁用法：
     *       LockUtils.getReentrantLock().lock();
     *       if (LockUtils.getReentrantLock().tryLock() {
     *          try {
     *              // 执行操作
     *                  }
     *          } finally {
     *              LockUtils.getReentrantLock().unlock();
     *          }
     *       } else {
     *           // 获取锁失败，处理失败逻辑
     *       }
     */
    public static Lock getReentrantLock() {
        return REENTRANT_LOCK;
    }

    /**
     * 线程局部锁用法：
     *      synchronized (LockUtils.getThreadLocalLock()) {}
     */
    public static Object getThreadLocalLock() {
        return THREAD_LOCAL_LOCK.get();
    }

}