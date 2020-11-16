package com.atguigu.gmall.test.juc8;

public class MyPhone {


    public  synchronized void sendMsg(){

        System.out.println("正在发短信。。。");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public  synchronized void call(){

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("正在打电话。。。");

    }

}
