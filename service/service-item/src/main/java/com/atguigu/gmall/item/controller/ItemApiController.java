package com.atguigu.gmall.item.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/item")
public class ItemApiController {

    @Autowired
    ItemService itemService;

    @RequestMapping("{skuId}")
    Result<Map<String,Object>> getItem(@PathVariable("skuId") Long skuId){
        // 调用product商品基础服务查询数据
        Map<String,Object> map = itemService.getItem(skuId);
        Map<String, Object> itemThread = itemService.getItemThread(skuId);
        return Result.ok(map);
    }
}
