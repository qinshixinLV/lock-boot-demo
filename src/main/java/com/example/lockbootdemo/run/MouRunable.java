package com.example.lockbootdemo.run;

import com.example.lockbootdemo.utils.RedisUtil;

public class MouRunable implements Runnable {
    
    private RedisUtil redisUtil;
    
    public MouRunable(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long result = redisUtil.deStock("moutai_stock", 1);
        if (result >= 0) {
            System.out.println("抢到:" + RedisUtil.count.addAndGet(1) + "瓶");
        } else if (result == -3) {
            redisUtil.initStock("moutai_stock", 200);
        }
       /* while (true) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Long result = redisUtil.deStock("moutai_stock", 1);
            if (result >= 0) {
                System.out.println("抢到:" + RedisUtil.count.addAndGet(1) + "瓶");
            } else if (result == -3) {
                redisUtil.initStock("moutai_stock", 2000);
            }
        }*/
    }
}
