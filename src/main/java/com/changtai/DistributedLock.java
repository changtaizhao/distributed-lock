package com.changtai;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-04 15:17
 */
public interface DistributedLock {

    /**
     * 阻塞获取锁
     * @return
     */
    boolean lock();

    /**
     * 尝试获取锁
     * @return
     */
    boolean tryLock();

    /**
     * 释放锁
     * @return
     */
    boolean releaseLock();

}
