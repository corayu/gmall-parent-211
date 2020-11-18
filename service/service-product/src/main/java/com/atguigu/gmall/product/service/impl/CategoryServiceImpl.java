package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    public List<BaseCategory1> getCategory1() {

        // 查询dao获得category1表的全部数据

        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(String category1Id) {

        QueryWrapper<BaseCategory2> baseCategory2QueryWrapper = new QueryWrapper<>();
        baseCategory2QueryWrapper.eq("category1_id",category1Id);

        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(baseCategory2QueryWrapper);

        return baseCategory2s;
    }

    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {
        QueryWrapper<BaseCategory3> baseCategory3QueryWrapper = new QueryWrapper<>();
        baseCategory3QueryWrapper.eq("category2_id",category2Id);

        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(baseCategory3QueryWrapper);

        return baseCategory3s;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {

        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(queryWrapper);

        return baseCategoryView;
    }

    @Override
    public List<JSONObject> getBaseCategoryList() {

        List<JSONObject> list = new ArrayList<>();

        // dao的分类BaseCategoryView集合
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);

        Map<Long,List<BaseCategoryView>> c1map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        Long index = 1l;// 页面用
        for (Map.Entry<Long, List<BaseCategoryView>> entry1  : c1map.entrySet()) {

            // 放入一级分类的JSONOBJECT
            JSONObject c1jsonObject = new JSONObject();

            Long c1Id = entry1.getKey();
            List<BaseCategoryView> c1CategoryView = entry1.getValue();

            c1jsonObject.put("index", index);
            c1jsonObject.put("categoryId",c1Id);
            c1jsonObject.put("categoryName",c1CategoryView.get(0).getCategory1Name());

            index++;

            // 放入二级分类的JSONOBJECT
            List<JSONObject> c2jsonObjectList = new ArrayList<>();
            Map<Long,List<BaseCategoryView>> c2map = c1CategoryView.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> entry2  : c2map.entrySet()) {
                Long c2Id = entry2.getKey();
                List<BaseCategoryView> c2CategoryViews = entry2.getValue();

                JSONObject c2jsonObject = new JSONObject();
                c2jsonObject.put("categoryId",c2Id);
                c2jsonObject.put("categoryName",c2CategoryViews.get(0).getCategory2Name());

                // 二级分类放入三级分类集合
                List<JSONObject> c3jsonObjectList = c2CategoryViews.stream().map(c2CategoryView->{
                    JSONObject c3jsonObject = new JSONObject();

                    Long category3Id = c2CategoryView.getCategory3Id();
                    String category3Name = c2CategoryView.getCategory3Name();

                    c3jsonObject.put("categoryId",category3Id);
                    c3jsonObject.put("categoryName",category3Name);
                    return c3jsonObject;
                }).collect(Collectors.toList());

                c2jsonObject.put("categoryChild",c3jsonObjectList);

                // 封装二级分类集合
                c2jsonObjectList.add(c2jsonObject);


            }
            // 一级分类放入二级分类集合
            c1jsonObject.put("categoryChild",c2jsonObjectList);

            // 封装一级分类集合
            list.add(c1jsonObject);
        }


        return list;
    }
}
