package com.atguigu.gmall.test.juc5;

public class Test {

    public static void main(String[] args) {

        FoodRoom foodRoom = new FoodRoom();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                foodRoom.cut();
            }
        },"切菜师傅").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                foodRoom.cook();
            }
        },"炒菜师傅").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                foodRoom.give();
                System.out.println("第"+i+"道菜完成。。。");
            }
        },"端菜师傅").start();
    }
}
