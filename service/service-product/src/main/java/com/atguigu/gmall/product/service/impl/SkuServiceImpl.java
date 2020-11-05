package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        // 保存sku基本信息
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();

        // 保存sku图片集合
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }

        // 保存sku对应平台属性集合
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        // 保存sku对应销售属性集合
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
    }

    @Override
    public IPage<SkuInfo> list(Page pageParam) {

        IPage iPage = skuInfoMapper.selectPage(pageParam, null);

        return iPage;
    }

    @Override
    public void onSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);

        // 将来要调用es插入已经上架的商品
    }

    @Override
    public void cancelSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);

        // 将来要调用es删除已经下架的商品

    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {

        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(queryWrapper);


        QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
        skuImageQueryWrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(skuImageQueryWrapper);

        skuInfo.setSkuImageList(skuImages);

        return skuInfo;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",skuId);
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    @Override
    public List<Map<String, Object>> getSkuValueIdsMap(Long spuId) {

        List<Map<String, Object>> map = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);

        return map;
    }
}
