package com.changtai.redis.standalone.jedis;

import com.changtai.DistributedLock;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Arrays;

/**
 * TODO
 *
 * @author zhaoct
 * @date 2020-11-04 15:16
 */
public class Lock1 implements DistributedLock {

    private String lockKey;
    private String requestId;
    private int expireTime;

    public Lock1(String lockKey, String requestId, int expireTime){
        this.lockKey = lockKey;
        this.requestId = requestId;
        this.expireTime = expireTime;
    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean tryLock() {
        Jedis jedis = new Jedis("localhost");
        SetParams params = new SetParams();
        params.nx().px(expireTime);
        String result = jedis.set(lockKey, requestId, params);
        if("OK".equals(result)){
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseLock() {
        Jedis jedis = new Jedis("localhost");
        String script =
                "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "return 0\n" +
                "end";
        Long result = (Long)jedis.eval(script, Arrays.asList(lockKey), Arrays.asList(requestId));
        if(1L == result){
            return true;
        }
        return false;
    }
}
