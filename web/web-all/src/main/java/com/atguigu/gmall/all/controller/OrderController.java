package com.atguigu.gmall.all.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @RequestMapping("trade.html")
    public String trade(Model model, HttpServletRequest request){

        String userId = request.getHeader("userId");

        // 生成交易码
        String tradeNo = UUID.randomUUID().toString().replaceAll("-","");


        Result<Map<String, Object>> result = orderFeignClient.trade();

        model.addAllAttributes(result.getData());

        // 结算页面放入交易码
        orderFeignClient.genTradeNo(tradeNo,userId);
        model.addAttribute("tradeNo",tradeNo);

        return "order/trade";
    }
}
