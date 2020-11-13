package com.atguigu.gmall.item.config;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        Vector vector = new Vector();
        vector.add(null);
        ArrayBlockingQueue<Object> objects = new ArrayBlockingQueue<Object>(3);
        objects.add("a");
        objects.add("b");
        objects.add("c");
        System.out.println(objects.element());
        Thread.sleep(2000);
        objects.remove();
        System.out.println(objects.element());
    }
}
