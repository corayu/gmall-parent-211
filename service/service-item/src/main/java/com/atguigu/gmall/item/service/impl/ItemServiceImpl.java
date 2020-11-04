package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;

    /***
     * 1 商品基本信息
     用缓存

     2 商品图片信息
     用缓存

     3 商品的销售属性信息
     用缓存

     4 商品分类信息
     用缓存

     5 商品的价格信息
     查库
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String, Object> map = new HashMap<>();
        //商品信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        //分类信息
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        //价格信息
        BigDecimal price = productFeignClient.getSkuPrice(skuId);

        //商品销售属性列表
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, spuId);

        map.put("categoryView", baseCategoryView);
        map.put("skuInfo", skuInfo);
        map.put("price",price);
        map.put("spuSaleAttrList",spuSaleAttrs);
        return map;
    }
}
