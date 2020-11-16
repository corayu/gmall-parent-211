package com.atguigu.gmall.test.juc10;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyScreen {

    private Object o;

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public void write(Object object){
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在执行写入操作");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            o = object;
        }finally {
            writeLock.unlock();
        }
    }


    public Object read(){
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在执行读取操作");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally {
            readLock.unlock();
        }
        return o;
    }

}
