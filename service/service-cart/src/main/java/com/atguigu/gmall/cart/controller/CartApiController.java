package com.atguigu.gmall.cart.controller;


import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @Autowired
    CartInfoService cartInfoService;

    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(HttpServletRequest request, @PathVariable("skuId") Long skuId, @PathVariable("isChecked") String isChecked, ModelMap modelMap) {
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }

        cartInfoService.checkCart(skuId, isChecked, userId);

        return Result.ok();
    }

    public BigDecimal getSumPrice(List<CartInfo> cartInfos) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            if (cartInfo.getIsChecked().equals("1")) {
                BigDecimal multiply = cartInfo.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                bigDecimal = bigDecimal.add(multiply);
            }
        }
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request, ModelMap modelMap) {

        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }

        List<CartInfo> cartInfos = cartInfoService.getCartList(userId);

        return Result.ok(cartInfos);
    }


    /**
     * 用户已经登录才能访问结算获得购物车列表转化成订单详情信息
     * @param userId
     * @return
     */
    @RequestMapping("inner/getCartList/{userId}")
    public List<CartInfo> getCartList(@PathVariable("userId") String userId) {
        List<CartInfo> cartInfos = cartInfoService.getCartList(userId);
        return cartInfos;
    }

    @RequestMapping("inner/addToCart/{skuId}/{skuNum}")
    void addToCart(HttpServletRequest request, @PathVariable("skuId") Long skuId, @PathVariable("skuNum") Long skuNum) {

        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }

        cartInfoService.addToCart(skuId, skuNum, userId);
    }

    @RequestMapping("inner/mergeToCartList/{userId}/{userTempId}")
    void mergeToCartList(@PathVariable("userId") String userId, @PathVariable("userTempId") String userTempId) {

        // 合并正式用户购物车
        cartInfoService.mergeToCartList(userId, userTempId);

        // 删除临时id购物车
        cartInfoService.deleteCartList(userTempId);

        // 同步缓存
        cartInfoService.loadCartCache(userId);

    }

    @RequestMapping("inner/checkIfMergeToCartList/{userTempId}")
    boolean checkIfMergeToCartList(@PathVariable("userTempId") String userTempId) {

        boolean b = false;

        List<CartInfo> cartInfos = cartInfoService.checkIfMergeToCartList(userTempId);

        if (null != cartInfos && cartInfos.size() > 0) {
            b = true;
        }

        return b;

    }
}
