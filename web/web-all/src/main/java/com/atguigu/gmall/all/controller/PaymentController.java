package com.atguigu.gmall.all.controller;


import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PaymentController {

    @RequestMapping("pay.html")
    public String pay(HttpServletRequest request, Model model, String orderId){

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(Long.parseLong(orderId));

        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

}
