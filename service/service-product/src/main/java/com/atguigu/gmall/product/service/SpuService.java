package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface SpuService {
    IPage<SpuInfo> index(Page pageParam, SpuInfo spuInfo);

    List<BaseSaleAttr> baseSaleAttrList();

    List<BaseTrademark> getTrademarkList();

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    List<SpuImage> spuImageList(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);
}
