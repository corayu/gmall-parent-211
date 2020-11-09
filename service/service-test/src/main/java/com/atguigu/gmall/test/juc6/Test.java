package com.atguigu.gmall.test.juc6;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*CallableImpl callable = new CallableImpl();
        FutureTask<Double> doubleFutureTask = new FutureTask<>(callable);
        new Thread(doubleFutureTask).start();
        Double aDouble = doubleFutureTask.get();
        System.out.println(aDouble);*/


        //在一个方法中分出三条线程分别查询3个商品接口

        //任务一
        CallableImpl jdcallable = new CallableImpl("jd");
        FutureTask<Double> JDTask = new FutureTask<Double>(jdcallable);
        new Thread(JDTask).start();

        //任务二
        CallableImpl tbcallable = new CallableImpl("tb");
        FutureTask<Double> TBTask = new FutureTask<Double>(tbcallable);
        new Thread(TBTask).start();

        //任务三
        CallableImpl pddcallable = new CallableImpl("pdd");
        FutureTask<Double> PddTask = new FutureTask<Double>(pddcallable);
        new Thread(PddTask).start();

        Double jdPrice = JDTask.get();
        Double tbPrice = TBTask.get();
        Double pddPrice = PddTask.get();
        System.out.println("京东价格"+jdPrice);
        System.out.println("淘宝价格"+tbPrice);
        System.out.println("拼多多价格"+pddPrice);
        System.out.println("这是主线程..");
    }
}
