package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;

import java.util.List;

public interface AttrInfoService {
    List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<SearchAttr> getAttrList(Long skuId);
}
