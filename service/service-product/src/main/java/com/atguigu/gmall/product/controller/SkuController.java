package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SkuController {

    @Autowired
    SkuService skuService;

    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){

        skuService.onSale(skuId);

        return Result.ok();
    }

    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){

        skuService.cancelSale(skuId);

        return Result.ok();
    }

    @RequestMapping("list/{page}/{size}")
    public Result list(@PathVariable Long page,@PathVariable Long size){

        Page pageParam = new Page(page ,size);

        IPage<SkuInfo> infoIPage = skuService.list(pageParam);

        return Result.ok(infoIPage);
    }

    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuService.saveSkuInfo(skuInfo);

        return Result.ok();
    }
    
    
}
