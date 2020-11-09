package com.atguigu.gmall.test.juc;

public class Test {
    public static void main(String[] args) {
        MyTicket myTicket = new MyTicket();
        for (int i=0;i<100;i++){
            MyRunnableImpl myRunnable = new MyRunnableImpl(myTicket);
            new Thread(myRunnable).start();
        }
        
        
        new Thread(()->{
            myTicket.sale();
        }).start();

       /* Thread thread = new Thread(){
            @Override
            public void run() {
                myTicket.sale();
            }
        };
        thread.start();*/
    }
}
