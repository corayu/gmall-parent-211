package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public IPage<SpuInfo> index(Page pageParam, SpuInfo spuInfo) {

        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("category3_id",spuInfo.getCategory3Id());

        IPage<SpuInfo> infoIPage = spuInfoMapper.selectPage(pageParam,queryWrapper);

        return infoIPage;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        // 保存spu，生成主键id
        spuInfoMapper.insert(spuInfo);
        Long spuId = spuInfo.getId();

        // 保存spu的销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insert(spuSaleAttr);
            // 保存spu的销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuId);
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }

        // 保存spu的图片信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuId);
            spuImageMapper.insert(spuImage);
        }
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {

        QueryWrapper<SpuSaleAttr> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("spu_id",spuId);

        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(queryWrapper);

        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {

            QueryWrapper<SpuSaleAttrValue> queryWrapperForValue = new QueryWrapper<>();

            queryWrapperForValue.eq("spu_id",spuId);
            queryWrapperForValue.eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(queryWrapperForValue);

            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);

        }

        return spuSaleAttrs;
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {

        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("spu_id",spuId);

        List<SpuImage> spuImages = spuImageMapper.selectList(queryWrapper);
        return spuImages;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId,Long spuId) {

        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId,spuId);

        return spuSaleAttrs;
    }
}
