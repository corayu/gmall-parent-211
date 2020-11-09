package com.atguigu.gmall.test.juc5;

public class Test {
    public static void main(String[] args) {
        FoodRoom foodRoom = new FoodRoom();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                foodRoom.cut();
                System.out.println("切菜师傅第"+i+"道菜");
            }

        }).start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                foodRoom.cook();
                System.out.println("炒菜师傅第"+i+"道菜");
            }

        }).start();


        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                foodRoom.give();
                System.out.println("端菜师傅第"+i+"道菜");
            }

        }).start();
    }
}
