package com.atguigu.gmall.product.controller;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuService skuService;

    @Autowired
    SpuService spuService;

    @Autowired
    AttrInfoService attrInfoService;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @RequestMapping("inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId) {
        SkuInfo skuInfo = skuService.getSkuInfoNx(skuId);
        return skuInfo;
    }

    @RequestMapping("inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id) {
        BaseCategoryView baseCategoryView = categoryService.getCategoryView(category3Id);
        return baseCategoryView;
    }

    @RequestMapping("inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId) {

        BigDecimal price = skuService.getSkuPrice(skuId);

        return price;
    }

    @RequestMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId) {

        List<SpuSaleAttr> spuSaleAttrs = spuService.getSpuSaleAttrListCheckBySku(skuId, spuId);

        return spuSaleAttrs;
    }

    @RequestMapping("inner/getSkuValueIdsMap/{spuId}")
    List<Map<String, Object>> getSkuValueIdsMap(@PathVariable("spuId") Long spuId) {

        List<Map<String, Object>> map = skuService.getSkuValueIdsMap(spuId);

        return map;
    }

    @RequestMapping("inner/getAttrList/{skuId}")
    List<SearchAttr> getAttrList(@PathVariable("skuId") Long skuId) {
        List<SearchAttr> baseAttrInfos = attrInfoService.getAttrList(skuId);

        return baseAttrInfos;
    }

    @RequestMapping("inner/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId") Long tmId) {
        BaseTrademark baseTrademark = baseTrademarkService.getTrademark(tmId);

        return baseTrademark;
    }

    @RequestMapping("inner/getBaseCategoryList")
    Result getBaseCategoryList(){

        List<JSONObject> baseCategoryList = categoryService.getBaseCategoryList();
        return Result.ok(baseCategoryList);
    }

}
