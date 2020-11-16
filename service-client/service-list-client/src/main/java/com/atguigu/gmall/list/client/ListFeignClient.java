package com.atguigu.gmall.list.client;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient("service-list")
public interface ListFeignClient {

    @RequestMapping("api/list/inner/upperGoods/{skuId}")
    Result upperGoods(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/inner/lowerGoods/{skuId}")
    Result lowerGoods(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/inner/incrHotScore/{skuId}")
    void incrHotScore(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list")
    Result<Map> list(@RequestBody SearchParam searchParam);
}
