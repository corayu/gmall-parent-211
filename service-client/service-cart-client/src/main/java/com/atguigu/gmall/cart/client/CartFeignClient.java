package com.atguigu.gmall.cart.client;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient("service-cart")
public interface CartFeignClient {

    @RequestMapping("api/cart/inner/addToCart/{skuId}/{skuNum}")
    void addToCart(@PathVariable("skuId") Long skuId,@PathVariable("skuNum")  Long skuNum);

    @RequestMapping("api/cart/inner/mergeToCartList/{userId}/{userTempId}")
    void mergeToCartList(@PathVariable("userId") String userId, @PathVariable("userTempId") String userTempId);

    @RequestMapping("api/cart/inner/checkIfMergeToCartList/{userTempId}")
    boolean checkIfMergeToCartList(@PathVariable("userTempId") String userTempId);

    @RequestMapping("api/cart/inner/getCartList/{userId}")
    public List<CartInfo> getCartList(@PathVariable("userId") String userId);
}
