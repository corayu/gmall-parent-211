package com.atguigu.gmall.test.juc;

public  class  MyTicket {

    private Long num = 100l;


    public synchronized Long sale(){

        num --;

        printing();


        return num;

    }

    private synchronized void printing() {
        System.out.println(Thread.currentThread().getName()+"买走了一张票，目前剩余票数："+num);
    }

}
