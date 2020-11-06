package com.changtai.redis.standalone.redisson;

import com.changtai.DistributedLock;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-04 17:55
 */
public class Lock2 implements DistributedLock {

    private RLock lock = null;
    private String lockKey;

    public Lock2(String lockKey){
        this.lockKey = lockKey;

        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        lock = redisson.getLock(lockKey);
    }

    @Override
    public boolean lock() {
        lock.lock();
        return true;
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean releaseLock() {
        lock.unlock();
        return true;
    }
}
