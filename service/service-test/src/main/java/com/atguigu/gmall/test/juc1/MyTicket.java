package com.atguigu.gmall.test.juc1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyTicket {

    private Long num = 100l;

    Lock lock = new ReentrantLock();

    public Long sale() {

        try {
            lock.lock();
            num--;
            System.out.println(Thread.currentThread().getName() + "买走了一张票，目前剩余票数：" + num);
        } finally {
            lock.unlock();
        }
        return num;
    }

    private void printing() {
        System.out.println(Thread.currentThread().getName() + "买走了一张票，目前剩余票数：" + num);
    }

}
