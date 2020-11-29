package com.atguigu.gmall.cart.test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestPrice {

    public static void main(String[] args) {

        // 数字和初始化
        BigDecimal b1 = new BigDecimal(0.01d);
        BigDecimal b2 = new BigDecimal(0.01f);
        BigDecimal b3 = new BigDecimal("0.01");

        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);

        // 数字的比较
        int i = b2.compareTo(b1);
        System.out.println(i);

        // 运算
        BigDecimal b4 = new BigDecimal("6");
        BigDecimal b5 = new BigDecimal("7");
        BigDecimal add = b4.add(b5);
        BigDecimal subtract = b4.subtract(b5);
        BigDecimal multiply = b4.multiply(b5);
        System.out.println(add);
        System.out.println(subtract);
        System.out.println(multiply);

        // 约数
        BigDecimal divide = b4.divide(b5,2, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(divide);

        BigDecimal add1 = b1.add(b2);

        BigDecimal bigDecimal = add1.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(bigDecimal);

    }
}
