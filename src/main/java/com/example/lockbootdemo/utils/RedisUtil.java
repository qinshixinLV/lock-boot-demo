package com.example.lockbootdemo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RedisUtil {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    public static final String STOCK_LUA;
    
    public static final String STOCK_INIT_LUA;
    
    public static AtomicInteger count = new AtomicInteger(0);
    
    static {
        /**
         *
         * @desc 扣减库存Lua脚本
         *
         * @params 库存key
         * @params 订单中商品数量
         * @return
         *      -3:库存未初始化
         *      -1:库存不足
         *      大于等于0:剩余库存（扣减之后剩余的库存）
         */
        StringBuilder sb = new StringBuilder();
        sb.append("if (redis.call('exists', KEYS[1]) == 1) then");
        sb.append("    local stock = tonumber(redis.call('get', KEYS[1]));");
        sb.append("    local num = tonumber(ARGV[1]);");
        sb.append("    if (stock >= num) then");
        sb.append("        return redis.call('decrby', KEYS[1], num);");
        sb.append("    else");
        sb.append("        return -1;");
        sb.append("    end;");
        sb.append("end;");
        sb.append("return -3;");
        STOCK_LUA = sb.toString();
        
        StringBuilder sb2 = new StringBuilder();
        sb2.append("if (redis.call('exists', KEYS[1]) == 1)");
        sb2.append("then");
        sb2.append("    return;");
        sb2.append("else");
        sb2.append("    redis.call('set', KEYS[1],ARGV[1]);");
        sb2.append("end;");
        STOCK_INIT_LUA = sb2.toString();
        
    }
    
    /**
     * 扣库存
     *
     * @param key 库存key
     * @param num 扣减库存数量
     * @return 扣减之后剩余的库存【-3:库存未初始化; -1:库存不足; 大于等于0:扣减库存之后的剩余库存】
     */
    public Long deStock(String key, int num) {
        return stringRedisTemplate
                .execute(RedisScript.of(STOCK_LUA, Long.class), Collections.singletonList(key), String.valueOf(num));
    }
    
    /**
     * 初始化库存
     *
     * @param key
     * @param num
     */
    public void initStock(String key, int num) {
        stringRedisTemplate.execute(RedisScript.of(STOCK_INIT_LUA, Long.class), Collections.singletonList(key),
                String.valueOf(num));
    }
    
}
