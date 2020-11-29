package com.atguigu.gmall.order.controller;


import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    OrderService orderService;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;


    @RequestMapping("inner/getOrderById/{orderId}")
    OrderInfo getOrderById(@PathVariable("orderId") String orderId){

        OrderInfo orderInfo = new OrderInfo();

        orderInfo = orderService.getOrderById(orderId);

        return orderInfo;
    }

    @RequestMapping("inner/genTradeNo/{tradeNo}/{userId}")
    void genTradeNo(@PathVariable("tradeNo") String tradeNo, @PathVariable("userId") String userId){

        orderService.genTradeNo(tradeNo,userId);

    }


    @RequestMapping("auth/submitOrder")
    public Result submitOrder(HttpServletRequest request, @RequestBody OrderInfo order,String tradeNo){

        String userId = request.getHeader("userId");
        boolean b = orderService.checkTradeNo(tradeNo,userId);
        if(b){

            order.setUserId(Long.parseLong(userId));

            order.setProcessStatus(ProcessStatus.UNPAID.getComment());
            order.setOrderStatus(OrderStatus.UNPAID.getComment());
            order.setCreateTime(new Date());
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DATE,1);// 24小时候过期
            Date time = instance.getTime();
            order.setExpireTime(time);
            order.setOrderComment("硅谷订单，永不收货");
            order.setTotalAmount(getTotalAmount(order.getOrderDetailList()));
            // 封装外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String outTradeNo = "atguigu"+System.currentTimeMillis()+sdf.format(new Date());
            order.setOutTradeNo(outTradeNo);

            // 提交订单需要，验价和验库存
            List<OrderDetail> orderDetailList = order.getOrderDetailList();
            for (OrderDetail orderDetail : orderDetailList) {
                // 验证价格
                BigDecimal orderPrice = orderDetail.getOrderPrice();
                BigDecimal price = orderPrice.divide(new BigDecimal(orderDetail.getSkuNum()), 2, BigDecimal.ROUND_HALF_DOWN);
                SkuInfo skuInfo = productFeignClient.getSkuInfo(orderDetail.getSkuId());
                BigDecimal skuPrice = skuInfo.getPrice();
                int i = price.compareTo(skuPrice);
                if(i!=0){
                    // 说明商品价格发生变化，重新让用户生成新的订单
                }

                // 验证库存

            }
            


            // 提交订单
            OrderInfo orderInfo = orderService.submitOrder(order);

            // 删除购物车

            return Result.ok(orderInfo.getId());
        }else {
            return Result.ok(ResultCodeEnum.FAIL);
        }

    }

    private BigDecimal getTotalAmount(List<OrderDetail> orderDetailList) {

        BigDecimal bigDecimal = new BigDecimal("0");

        for (OrderDetail orderDetail : orderDetailList) {
            bigDecimal = bigDecimal.add(orderDetail.getOrderPrice());
        }

        return bigDecimal;
    }


    @RequestMapping("inner/trade")
    public Result<Map<String,Object>> trade(HttpServletRequest request){

        String userId = AuthContextHolder.getUserId(request);


        List<UserAddress> userAddresses =  userFeignClient.getUserAddresses(userId);

        List<CartInfo> cartInfos = cartFeignClient.getCartList(userId);

        Map<String,Object> map = new HashMap<>();
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {

            if(cartInfo.getIsChecked()==1){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                Integer skuNum = cartInfo.getSkuNum();
                SkuInfo skuInfo = productFeignClient.getSkuInfo(cartInfo.getSkuId());

                orderDetail.setOrderPrice(skuInfo.getPrice().multiply(new BigDecimal(skuNum)));
                orderDetails.add(orderDetail);

                totalAmount = totalAmount.add(orderDetail.getOrderPrice());
            }

        }
        map.put("detailArrayList",orderDetails);
        map.put("userAddressList", userAddresses);
        map.put("totalAmount",totalAmount);
        return Result.ok(map);
    }

}
