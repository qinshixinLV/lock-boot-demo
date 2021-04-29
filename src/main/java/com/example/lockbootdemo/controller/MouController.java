package com.example.lockbootdemo.controller;

import com.example.lockbootdemo.run.MouRunable;
import com.example.lockbootdemo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MouController {
    
    @Autowired
    private RedisUtil redisUtil;
    
    private int count = 0;
    
    private static long start = 0;
    
    private static long end = 0;
    
    @GetMapping("snap")
    public void snap() {
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long result = redisUtil.deStock("moutai_stock", 1);
        if (result >= 0) {
            System.out.println("线程:" + count++ + "  抢到第:" + RedisUtil.count.addAndGet(1) + "瓶");
        } else if (result == -1) {
            System.out.println("线程:" + count++ + "  没抢到");

            /*redisUtil.initStock("moutai_stock", 500);
            Long result2 = redisUtil.deStock("moutai_stock", 1);
            System.out.println("抢到:" + RedisUtil.count.addAndGet(1) + "瓶");*/
        }
        if (result == 99) {
            start = System.currentTimeMillis();
        } else if (result == 0) {
            end = System.currentTimeMillis();
            System.out.println("耗时:" + (end - start));
        }
    }
    
}
