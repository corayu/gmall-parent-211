package com.atguigu.gmall.test.juc10;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Test2 {

    public static void main(String[] args) {
        // 初始化3个停车位
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 6; i++) {
            int num = i;
            new Thread(()->{
                try {
                    semaphore.acquire();
                    System.out.println(num+"号车进入车库。。。");
                    Random r = new Random();
                    try {
                        Thread.sleep(r.nextInt(10) * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(num+"===========号车驶出车库==========");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }

            }).start();
        }

    }
}
