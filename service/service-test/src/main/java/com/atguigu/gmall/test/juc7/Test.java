package com.atguigu.gmall.test.juc7;

public class Test {

    public static void main(String[] args) {
        Runnable r = new RunnableImpl();
        new Thread(r).start();

        new Thread(()->{

        }).start();
    }
}
