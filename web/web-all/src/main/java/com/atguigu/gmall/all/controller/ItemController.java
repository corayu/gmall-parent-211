package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;


    @RequestMapping("test")
    public String test(ModelMap modelMap, HttpServletRequest request, HttpSession session) {
        modelMap.put("hello","hello thymeleaf");
        modelMap.put("flag","1");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("元素"+i);
        }
        modelMap.put("list",list);
        modelMap.put("num",1);

        request.setAttribute("gname","<span style=\"color:green\">宝强</span>");
        session.setAttribute("user","user");
        return "test";
    }


    /**
     * sku详情页面
     *
     * @param skuId
     * @param model
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model) {

        // 查询分类数据,item查询的是汇总数据,包括分类集合，商品详情，图片集合，销售属性集合等等
        Result<Map<String,Object>> result = itemFeignClient.getItem(skuId);
        model.addAllAttributes(result.getData());//result.getData()返回的是一个map集合

        return "item/index";//index.html
    }

}


