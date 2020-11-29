package com.atguigu.gmall.order.client;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("service-order")
public interface OrderFeignClient {

    @RequestMapping("api/order/inner/trade")
    Result<Map<String,Object>> trade();

    @RequestMapping("api/order/inner/genTradeNo/{tradeNo}/{userId}")
    void genTradeNo(@PathVariable("tradeNo") String tradeNo, @PathVariable("userId") String userId);

    @RequestMapping("api/order/inner/getOrderById/{orderId}")
    OrderInfo getOrderById(@PathVariable("orderId") String orderId);
}
