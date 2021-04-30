package com.example.lockbootdemo.service;

import com.example.lockbootdemo.utils.LockRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LockOnServiceImpl implements LockOnService {
    
    @Autowired
    private LockRedis lockRedis;
    
    private int num = 100;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
   /* @Override
    public void lockOn() {
        String uuid = String.valueOf(UUID.randomUUID());
        Long lock_result = lockRedis.lock("lock_order", uuid, 5);
        
        try {
            if (lock_result == 1) {
                System.out.println("lock_result为:"+lock_result);
                if (num > 0) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    num = num - 1;
                    System.out.println("num减一之后为:" + num);
                }
            }
        } finally {
            // 释放锁
            lockRedis.unlock("lock_order", uuid);
        }
        
    }*/
    
    @Override
    public void lockOn() {
        String uuid = String.valueOf(UUID.randomUUID());
        Long lock_result = lockRedis.lock("lock_order", uuid, 5);
        
        if (lock_result == 1) {
            try {
                System.out.println("获取到锁:" + uuid);
                Long moutai_stock = Long.valueOf(stringRedisTemplate.opsForValue().get("moutai_stock"));
                if (moutai_stock > 0) {
                       /* try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    stringRedisTemplate.opsForValue().decrement("moutai_stock");
                }
            } finally {
                // 释放锁
                lockRedis.unlock("lock_order", uuid);
                if (lock_result == 1) {
                    System.out.println("释放锁:" + uuid);
                }
            }
        }
        
    }
}
