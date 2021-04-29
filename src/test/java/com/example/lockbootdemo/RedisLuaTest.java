package com.example.lockbootdemo;

import com.example.lockbootdemo.run.MouRunable;
import com.example.lockbootdemo.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisLuaTest extends LockBootDemoApplicationTests {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Test
    void name() {
        String name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }
    
    @Test
    void name2() {
        Long result = redisUtil.deStock("moutai_stock", 1);
        System.out.println("result为:" + result);
        if (result == -3) {
            redisUtil.initStock("moutai_stock", 1000);
        }
    }
    
    @Test
    void name3() {
        for (int i = 0; i < 1000; i++) {
            new Thread(new MouRunable(redisUtil)).start();
        }
        System.out.println("线程启动完毕!");
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
