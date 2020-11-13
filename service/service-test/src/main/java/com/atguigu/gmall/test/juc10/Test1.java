package com.atguigu.gmall.test.juc10;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Test1 {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("同学们正在上自习");
        CountDownLatch countDownLatch = new CountDownLatch(50);
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                Random r = new Random();
                try {
                    Thread.sleep(r.nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+"上完自习，离开教室");
                countDownLatch.countDown();
            }, "同学"+i).start();
        }
        countDownLatch.await();
        System.out.println("上完自习，锁门");
    }
}
