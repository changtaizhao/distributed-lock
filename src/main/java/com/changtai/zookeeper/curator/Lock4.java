package com.changtai.zookeeper.curator;

import com.changtai.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-05 11:22
 */
public class Lock4 implements DistributedLock {

    private InterProcessMutex lock;

    public Lock4(){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3,Integer.MAX_VALUE);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();
        lock = new InterProcessMutex(client,"/lock4");
    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean tryLock() {
        try {
            lock.acquire();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {

        }
    }

    @Override
    public boolean releaseLock() {
        try {
            lock.release();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {

        }
    }
}
