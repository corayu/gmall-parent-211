package com.atguigu.gmall.test.juc8;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        MyPhone myPhone = new MyPhone();

        MyPhone myPhone1 = new MyPhone();

        new Thread(()->{
            myPhone.sendMsg();
        }).start();


        Thread.sleep(1000);

        new Thread(()->{
            myPhone.call();
        }).start();
    }
}
