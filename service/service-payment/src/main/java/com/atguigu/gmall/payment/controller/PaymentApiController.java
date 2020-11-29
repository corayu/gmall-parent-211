package com.atguigu.gmall.payment.controller;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/")
public class PaymentApiController {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    OrderFeignClient orderFeignClient;

    @Autowired
    PaymentInfoService paymentInfoService;

    @RequestMapping("alipay/callback/return")
    public String callbackReturn(HttpServletRequest request){

        String queryString = request.getQueryString();
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");

        String sign = request.getParameter("sign");

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setCallbackContent(queryString);
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setOutTradeNo(out_trade_no);

        paymentInfoService.updatePayment(paymentInfo);

        return "订单支付成功";
    }


    @RequestMapping("alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId") String orderId){


        OrderInfo orderInfo = orderFeignClient.getOrderById(orderId);

        String form =  paymentInfoService.alipaySubmit(orderInfo);

        return form;
    }

}
