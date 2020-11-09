package com.atguigu.gmall.test.juc8;

public class MyPhone {
    public synchronized void sendMsg() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信...");
    }

    public synchronized void call() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("打电话...");
    }
}
