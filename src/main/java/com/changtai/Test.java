package com.changtai;

import com.changtai.redis.standalone.jedis.Lock1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-04 15:45
 */
public class Test {

    /**
     * 共享资源
     */
    public static volatile int index = 1;

    public static void main(String[] args) {
       BusinessService service = new BusinessService();
       ExecutorService executorService = Executors.newFixedThreadPool(2);
       for(int i=0; i<10; i++){
           executorService.submit(()->{
               service.doService();
           });
       }
    }
}
