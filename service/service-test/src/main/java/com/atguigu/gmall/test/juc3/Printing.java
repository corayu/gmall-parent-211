package com.atguigu.gmall.test.juc3;

public class Printing {

    private long num = 0;

    public synchronized void print0(){
        while(num!=1){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num--;
        System.out.println(Thread.currentThread().getName()+"执行方法0，打印："+num);

        notifyAll();
    }

    public synchronized void print1(){
        while(num!=0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;
        System.out.println(Thread.currentThread().getName()+"执行方法1，打印："+num);

        notifyAll();
    }

}
