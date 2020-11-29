package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

public interface OrderService {
    OrderInfo submitOrder(OrderInfo order);

    boolean checkTradeNo(String tradeNo,String userId);

    void genTradeNo(String tradeNo, String userId);

    OrderInfo getOrderById(String orderId);
}
