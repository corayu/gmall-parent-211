package com.atguigu.gmall.test.juc10;

public class Test {

    public static void main(String[] args) {

        MyScreen myScreen = new MyScreen();

        new Thread(()->{
            myScreen.write("下课。。。");
        },"体育老师").start();

        new Thread(()->{
            myScreen.write("上课。。。");
        },"数学老师").start();

        for (int i = 0; i < 50; i++) {
            new Thread(()->{
                Object read = myScreen.read();
                System.out.println("接收内容"+read);
            }).start();
        }

    }

}
