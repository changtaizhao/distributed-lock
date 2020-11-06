package com.changtai.redis.cluster.redisson;

import com.changtai.DistributedLock;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-05 10:53
 */
public class Lock3 implements DistributedLock {

    private RedissonRedLock multiLock;

    public Lock3() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        RLock lock1 = redisson.getFairLock("lock3-1");
        RLock lock2 = redisson.getFairLock("lock3-2");
        RLock lock3 = redisson.getFairLock("lock3-3");
        multiLock = new RedissonRedLock(lock1, lock2, lock3);
    }

    @Override
    public boolean lock() {
        multiLock.lock();
        return true;
    }

    @Override
    public boolean tryLock() {
        return multiLock.tryLock();
    }

    @Override
    public boolean releaseLock() {
        multiLock.unlock();
        return true;
    }
}
