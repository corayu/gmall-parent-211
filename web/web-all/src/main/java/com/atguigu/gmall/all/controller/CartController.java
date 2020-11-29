package com.atguigu.gmall.all.controller;


import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;


        @RequestMapping("cart.html")
    public String cartList(HttpServletRequest request, ModelMap modelMap){
//
//        String userId = AuthContextHolder.getUserId(request);
//        String userTempId = AuthContextHolder.getUserTempId(request);
//
//        if(StringUtils.isEmpty(userId)){
//            userId = userTempId;
//        }

        return "cart/index";
    }


    @RequestMapping("addCart.html")
    public ModelAndView addCart(Long skuId ,Long skuNum){

        // 调用购物车的添加业务
        cartFeignClient.addToCart(skuId,skuNum);

        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("skuNum",0);
        modelAndView.addObject("skuName",skuInfo.getSkuName());
        modelAndView.addObject("skuDefaultImg",skuInfo.getSkuDefaultImg());
        modelAndView.addObject("price",skuInfo.getPrice());

        modelAndView.setViewName("redirect:http://cart.gmall.com/cartSuccess");

        return modelAndView;
    }

    @RequestMapping("cartSuccess")
    public String cartSuccess(SkuInfo skuInfo, Long skuNum, ModelMap modelMap){

        modelMap.put("skuInfo",skuInfo);
        modelMap.put("skuNum",skuNum);

        return "cart/addCart";
    }


}
