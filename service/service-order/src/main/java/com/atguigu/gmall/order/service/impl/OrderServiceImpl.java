package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 生成tradeNo
     * @param tradeNo
     * @param userId
     */
    @Override
    public void genTradeNo(String tradeNo, String userId) {

        redisTemplate.opsForValue().set(RedisConst.USER_KEY_PREFIX+userId+":tradeNo",tradeNo);

    }

    @Override
    public OrderInfo getOrderById(String orderId) {

        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);

        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("order_id",orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper);
        orderInfo.setOrderDetailList(orderDetails);

        return orderInfo;
    }

    @Override
    public OrderInfo submitOrder(OrderInfo order) {
        orderInfoMapper.insert(order);

        List<OrderDetail> orderDetailList = order.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(order.getId());

            orderDetailMapper.insert(orderDetail);
        }

        return order;
    }

    /**
     * 比较tradeNo
     * @param tradeNo
     * @param userId
     * @return
     */
    @Override
    public boolean checkTradeNo(String tradeNo,String userId) {

        boolean b = false;

        String tradeNoFromCache = (String)redisTemplate.opsForValue().get(RedisConst.USER_KEY_PREFIX+userId+":tradeNo");

        if(!StringUtils.isEmpty(tradeNoFromCache)){
            if(tradeNo.equals(tradeNoFromCache)){
                b = true;

                // 删除tradeNo
                redisTemplate.delete(RedisConst.USER_KEY_PREFIX+userId+":tradeNo");
            }
        }

        return b;
    }
}
