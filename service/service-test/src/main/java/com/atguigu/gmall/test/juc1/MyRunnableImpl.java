package com.atguigu.gmall.test.juc1;

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
