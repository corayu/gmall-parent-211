package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttrInfoServiceImpl implements AttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id) {

        QueryWrapper<BaseAttrInfo> baseAttrInfoQueryWrapper = new QueryWrapper<>();
        baseAttrInfoQueryWrapper.eq("category_level",3);
        baseAttrInfoQueryWrapper.eq("category_id",category3Id);
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(baseAttrInfoQueryWrapper);

        for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id",baseAttrInfo.getId());
            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapper);
            baseAttrInfo.setAttrValueList(baseAttrValues);
        }
        
        return baseAttrInfos;
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long attrId = baseAttrInfo.getId();

        if(null!=attrId&&attrId>0){
            baseAttrInfoMapper.updateById(baseAttrInfo);
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id",attrId);
            baseAttrValueMapper.delete(queryWrapper);
        }else {
            baseAttrInfoMapper.insert(baseAttrInfo);
            attrId = baseAttrInfo.getId();
        }

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if(null!=attrValueList&&attrValueList.size()>0){
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(attrId);
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }
    @Override
    public List<SearchAttr> getAttrList(Long skuId) {

        List<SearchAttr> baseAttrInfos = baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);

        return baseAttrInfos;
    }
}
