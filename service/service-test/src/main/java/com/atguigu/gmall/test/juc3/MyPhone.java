package com.atguigu.gmall.test.juc3;

public class MyPhone {

    public static void main(String[] args) throws InterruptedException {
        MyPhone myPhone = new MyPhone();

        new Thread(()->{
            myPhone.call();
        }).start();

        Thread.sleep(1000);

        new Thread(()->{
            myPhone.sendMessage();
        }).start();




    }



    public synchronized void sendMessage(){

        System.out.println("正在发短信");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void call(){

        System.out.println("正在打电话");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
