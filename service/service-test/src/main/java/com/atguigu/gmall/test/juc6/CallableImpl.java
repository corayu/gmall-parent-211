package com.atguigu.gmall.test.juc6;

import java.util.concurrent.Callable;

public class CallableImpl implements Callable<Double> {
    private String mall = "";

    public CallableImpl(String mall) {
        this.mall = mall;
    }

    @Override
    public Double call() throws Exception {
        Double price = null;
        if (mall.equals("jd")) {
            price = 100d;
            System.out.println("查询京东是..."+price);
        } else if (mall.equals("tb")) {
            price = 200d;
            System.out.println("查询淘宝..."+price);
        } else if (mall.equals("pdd")) {
            price = 300d;
            Thread.sleep(2000);
            System.out.println("查询拼多多..."+price);
        }
        return price;
    }

    
    /*@Override
    public Double call() throws Exception {
        System.out.println("正在计算商品价格啊");
        return 0d;  
    }*/
}
