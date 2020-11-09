package com.atguigu.gmall.test.juc4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Printing {

    private long num = 0;
    Lock lock = new ReentrantLock();
    Condition condition0 = lock.newCondition();
    Condition condition1 = lock.newCondition();

    public void print0() {
        lock.lock();
        try {
            while (num!=1) {
                try {
                    condition0.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            num--;
            System.out.println(Thread.currentThread().getName()+"执行方法0，打印："+num);
            condition1.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print1() {
        lock.lock();
        try {
            while (num!=0) {
                try {
                    condition1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            num++;
            System.out.println(Thread.currentThread().getName()+"执行方法1，打印："+num);
            condition0.signal();

        } finally {
            lock.unlock();
        }
    }

}
