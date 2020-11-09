package com.atguigu.gmall.test.juc4;

public class Test {

    public static void main(String[] args) {

        Printing printing = new Printing();

        new Thread(()->{
            for (int i = 0; i < 25; i++) {
                printing.print0();
            }
        },"打印0的线程1").start();

        new Thread(()->{
            for (int i = 0; i < 25; i++) {
                printing.print0();
            }
        },"打印0的线程2").start();


        new Thread(()->{
            for (int i = 0; i < 25; i++) {
                printing.print1();
            }
        },"打印1的线程1").start();


        new Thread(()->{
            for (int i = 0; i < 25; i++) {
                printing.print1(); 
            }
        },"打印1的线程2").start();
 
    }
}
