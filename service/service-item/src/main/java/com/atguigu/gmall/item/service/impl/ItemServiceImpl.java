package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ListFeignClient listFeignClient;

    @Autowired
    ThreadPoolExecutor executor;



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
    public Map<String, Object> getItemThread(Long skuId) {

        long currentTimeMillisStart = System.currentTimeMillis();
        System.out.println("多线程执行开始:"+currentTimeMillisStart);

        Map<String, Object> map = new HashMap<>();


        // 商品信息/图片信息
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        },executor);

        // 分类信息
        CompletableFuture completableFutureCategory = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {

                BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", baseCategoryView);
            }
        },executor);

        // 商品的销售属性值对应skuId的map
        CompletableFuture completableFutureSaleMap = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<Map<String, Object>> valueSkuIdMapList = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
                Map<String,String> valueSkuIdMap = new HashMap<>();
                for (Map<String, Object> stringObjectMap : valueSkuIdMapList) {
                    String v_sku_id = stringObjectMap.get("sku_id")+"";
                    String k_value_ids = stringObjectMap.get("value_ids")+"";
                    valueSkuIdMap.put(k_value_ids,v_sku_id);
                }
                map.put("valuesSkuJson", JSON.toJSONString(valueSkuIdMap));
            }
        },executor);


        // 商品销售属性列表
        CompletableFuture completableFutureSaleAttr = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Long spuId = skuInfo.getSpuId();
                List<SpuSaleAttr> spuSaleAttrs =  productFeignClient.getSpuSaleAttrListCheckBySku(skuId,spuId);
                map.put("spuSaleAttrList",spuSaleAttrs);
            }
        });


        // 价格信息
        CompletableFuture completableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getSkuPrice(skuId);
                map.put("price",price);
            }
        },executor);

        // 调用商品搜索服务，更新热度值
        CompletableFuture completableFutureHotScore = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
               listFeignClient.incrHotScore(skuId);
            }
        },executor);


        // 线程组合，主线程等待所有线程结束之后在向后执行
        CompletableFuture.allOf(completableFutureSkuInfo,completableFutureCategory,completableFutureSaleMap,completableFutureSaleAttr,completableFuturePrice).join();

        long currentTimeMillisEnd = System.currentTimeMillis();
        System.out.println("多线程执行结束:"+currentTimeMillisEnd);

        System.out.println("多线程执行用时:"+(currentTimeMillisEnd-currentTimeMillisStart));

        return map;
    }


    @Override
    public Map<String, Object> getItem(Long skuId) {

        long currentTimeMillisStart = System.currentTimeMillis();
        System.out.println("非多线程执行开始:"+currentTimeMillisStart);

        Map<String, Object> map = new HashMap<>();

        // 商品信息/图片信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        // 分类信息
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        // 价格信息
        BigDecimal price = productFeignClient.getSkuPrice(skuId);

        // 商品销售属性列表
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrs =  productFeignClient.getSpuSaleAttrListCheckBySku(skuId,spuId);

        // 商品的销售属性值对应skuId的map
        List<Map<String, Object>> valueSkuIdMapList = productFeignClient.getSkuValueIdsMap(spuId);

        Map<String,String> valueSkuIdMap = new HashMap<>();

        for (Map<String, Object> stringObjectMap : valueSkuIdMapList) {
            String v_sku_id = stringObjectMap.get("sku_id")+"";
            String k_value_ids = stringObjectMap.get("value_ids")+"";
            valueSkuIdMap.put(k_value_ids,v_sku_id);
        }

        map.put("categoryView", baseCategoryView);
        map.put("skuInfo",skuInfo);
        map.put("price",price);
        map.put("spuSaleAttrList",spuSaleAttrs);
        map.put("valuesSkuJson", JSON.toJSONString(valueSkuIdMap));

        long currentTimeMillisEnd = System.currentTimeMillis();
        System.out.println("非多线程执行结束:"+currentTimeMillisEnd);

        System.out.println("非多线程执行用时:"+(currentTimeMillisEnd-currentTimeMillisStart));

        return map;
    }

}
