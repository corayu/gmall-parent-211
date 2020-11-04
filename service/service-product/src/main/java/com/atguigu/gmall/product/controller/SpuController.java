package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuController {
    @Autowired
    SpuService spuService;


    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){

        List<SpuSaleAttr> spuSaleAttrs = spuService.spuSaleAttrList(spuId);

        return Result.ok(spuSaleAttrs);
    }

    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){

        List<SpuImage> spuImages = spuService.spuImageList(spuId);

        return Result.ok(spuImages);
    }



    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        spuService.saveSpuInfo(spuInfo);

        return Result.ok();
    }


    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){

        List<BaseTrademark> baseTrademarks = spuService.getTrademarkList();

        return Result.ok(baseTrademarks);
    }

    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrs = spuService.baseSaleAttrList();

        return Result.ok(baseSaleAttrs);
    }

    @GetMapping("{page}/{size}")
    public Result<IPage<SpuInfo>> index(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable Long page,
                                        @ApiParam(name = "size", value = "每页记录数", required = true) @PathVariable Long size,
                                        @ApiParam(name = "spuInfo", value = "查询对象", required = false) SpuInfo spuInfo) {

        Page pageParam = new Page(page ,size);

        IPage<SpuInfo> infoIPage = spuService.index(pageParam,spuInfo);

        return Result.ok(infoIPage);
    }
}
