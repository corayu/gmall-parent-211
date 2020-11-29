package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartInfoService {
    void addToCart(Long skuId, Long skuNum,String userId);

    void mergeToCartList(String userId, String userTempId);

    void deleteCartList(String userTempId);

    List<CartInfo> checkIfMergeToCartList(String userTempId);

    List<CartInfo>  loadCartCache(String userId);

    List<CartInfo> getCartList(String userId);

    void checkCart(Long skuId, String isChecked,String userId);

    void genTradeNo(String tradeNo, String userId);
}
