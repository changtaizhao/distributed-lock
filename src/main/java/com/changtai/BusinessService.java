package com.changtai;

import com.changtai.mysql.Lock5;
import com.changtai.redis.cluster.redisson.Lock3;
import com.changtai.redis.standalone.jedis.Lock1;
import com.changtai.redis.standalone.redisson.Lock2;
import com.changtai.zookeeper.curator.Lock4;

import java.util.UUID;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-04 16:27
 */
public class BusinessService {

    public void doService(){

//        String requestId = UUID.randomUUID().toString();
//        DistributedLock lock = new Lock1("lock1", requestId, 5000);

//        DistributedLock lock = new Lock2("lock2");

//        DistributedLock lock = new Lock3();

//        DistributedLock lock = new Lock4();

        DistributedLock lock = new Lock5();


        //lock
        boolean lockFlag = lock.tryLock();
//        boolean lockFlag = lock.lock();
        if(lockFlag){
            System.out.println(Thread.currentThread().getName() + " ... 获取锁成功");
        }else{
            System.out.println(Thread.currentThread().getName() + " ... 获取锁失败");

            //获取失败后，等一会在重新获取
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return;
        }

        //do some business
        try {
            System.out.println("做一些工作...");
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //unlock
            boolean releaseFlag = lock.releaseLock();
            if(releaseFlag){
                System.out.println(Thread.currentThread().getName() + " ... 释放锁成功");
            }else {
                System.out.println(Thread.currentThread().getName() + " ... 释放锁失败");
            }
        }

        //锁释放后，等一会在重新获取
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
