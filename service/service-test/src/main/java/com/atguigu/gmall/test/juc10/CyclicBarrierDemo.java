package com.atguigu.gmall.test.juc10;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    private static final int NUMBER = 7;

    public static void main(String[] args) {
        // 集齐7颗龙珠
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> {
            System.out.println("*****集齐7颗龙珠就可以召唤神龙");
        });

        for (int i = 1; i <= 7; i++) {
            int num = i;
            new Thread(() -> {
                try {
                    Random r = new Random();
                    try {
                        Thread.sleep(r.nextInt(5) * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "\t 星龙珠被收集 ");
                    System.out.println("await：" + num);
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }


    }
}
 