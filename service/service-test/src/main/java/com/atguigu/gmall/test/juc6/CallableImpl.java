package com.atguigu.gmall.test.juc6;

import java.util.concurrent.Callable;

public class CallableImpl implements Callable<Double> {

    private String mall = "";

    public CallableImpl(String mall) {
        this.mall = mall;
    }

    @Override
    public Double call() throws Exception {
        //System.out.println("正在计算商品价格。。。");

        Double price = 0d;

        if (mall.equals("jd")) {
            System.out.println("正在查询京东价格。。。");
            price = 102d;
        } else if (mall.equals("tb")) {
            System.out.println("正在查询淘宝价格。。。");
            price = 101d;
        } else {
            System.out.println("正在查询拼多多价格。。。");
            Thread.sleep(2000);
            price = 10d;
        }

        return price;
    }

}
