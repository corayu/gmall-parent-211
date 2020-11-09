package com.atguigu.gmall.test.juc8;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        /*
        * 同一个内存对象myPhone里的两个synchronized方法call/sendMsg 
        */
        MyPhone myPhone = new MyPhone();
        new Thread(() -> {
            myPhone.sendMsg();
        }).start();

        Thread.sleep(1000);

        new Thread(() -> {
            myPhone.call();
        }
        ).start();
    }
}
