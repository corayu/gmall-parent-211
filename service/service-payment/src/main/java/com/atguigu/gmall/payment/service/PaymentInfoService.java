package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

public interface PaymentInfoService {
    String  alipaySubmit(OrderInfo orderInfo);

    void updatePayment(PaymentInfo paymentInfo);
}
