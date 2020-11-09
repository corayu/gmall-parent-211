package com.atguigu.gmall.test.juc;

public class MyRunnableImpl implements Runnable {
    MyTicket myTicket;

    public MyRunnableImpl(MyTicket myTicket) {
        this.myTicket = myTicket;
    }
    
    @Override
    public void run() {
        this.myTicket.sale();    
    }
}
