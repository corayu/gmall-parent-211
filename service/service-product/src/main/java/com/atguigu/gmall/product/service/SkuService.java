package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuService {
/*

    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Page pageParam);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuInfo(Long skuId);
    BigDecimal getSkuPrice(Long skuId);

    List<Map<String, Object>> getSkuValueIdsMap(Long spuId);

*/

    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Page pageParam);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuInfoNx(Long skuId);

    SkuInfo getSkuInfo(Long skuId);

    BigDecimal getSkuPrice(Long skuId);

    List<Map<String, Object>> getSkuValueIdsMap(Long spuId);
}
