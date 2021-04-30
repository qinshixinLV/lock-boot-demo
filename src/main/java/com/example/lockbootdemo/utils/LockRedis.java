package com.example.lockbootdemo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class LockRedis {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    public static final String LOCK_LUA;
    
    public static final String ON_LOCK_LUA;
    
    static {
        /**
         * 加锁lua
         */
        StringBuilder sb = new StringBuilder();
        sb.append("local set_result = redis.call('setnx',KEYS[1],ARGV[1]);");
        sb.append("if(set_result == 1) then ");
        sb.append("    return redis.call('expire',KEYS[1],ARGV[2]);");
        sb.append("else");
        sb.append("    return -1;");
        sb.append("end;");
        LOCK_LUA = sb.toString();
        
        /**
         * 解锁lua
         */
        StringBuilder sb2 = new StringBuilder();
        sb2.append("if(redis.call('get',KEYS[1]) == ARGV[1]) then");
        sb2.append("    return redis.call('del',KEYS[1]);");
        sb2.append("else ");
        sb2.append("    return 1;");
        sb2.append("end;");
        ON_LOCK_LUA = sb2.toString();
    }
    
    /**
     * 加锁操作
     *
     * @param key     key值
     * @param value   value值，解锁时需要校验value值是否符合预期，负责会误删不是自己的锁
     * @param exptime 过期时间
     * @return 1  成功 -1 失败 0 加锁成功但是设置过期时间失败，此时不要忘记手动释放锁
     */
    public Long lock(String key, String value, int exptime) {
        return stringRedisTemplate
                .execute(RedisScript.of(LOCK_LUA, Long.class), Collections.singletonList(key), value, String.valueOf(exptime));
    }
    
    /**
     * 释放锁
     *
     * @param key   key值
     * @param value value值，解锁时需要校验value值是否符合预期，负责会误删不是自己的锁
     * @return 1 成功 0 失败
     */
    public Long unlock(String key, String value) {
        return stringRedisTemplate
                .execute(RedisScript.of(ON_LOCK_LUA, Long.class), Collections.singletonList(key), value);
    }
    
}
