package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class ListController {


    @Autowired
    private ListFeignClient listFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;


    @GetMapping({"/","index"})
    public String index(SearchParam searchParam, Model model) {
        Result result = productFeignClient.getBaseCategoryList();

        model.addAttribute("list",result.getData());
        return "index";
    }

    /**
     * 列表搜索
     * @param searchParam
     * @return
     */
    @GetMapping({"search.html","list.html"})
    public String list(SearchParam searchParam, Model model) {
        Result<Map> result = listFeignClient.list(searchParam);
        model.addAllAttributes(result.getData());

//        String[] props = searchParam.getProps();
//        Map data = result.getData();

//        List<SearchResponseAttrVo> searchResponseAttrVos = (List<SearchResponseAttrVo>)data.get("attrsList");
//        for (String prop : props) {
//            Iterator<SearchResponseAttrVo> iterator = searchResponseAttrVos.iterator();
//            while(iterator.hasNext()){
//                if(prop.equals(iterator.next().getAttrId())){
//                    iterator.remove();
//                }
//            }
//        }
//


        // 当前请求url
        model.addAttribute("urlParam",makeUrlParam(searchParam));

        // 排序
        if(StringUtils.isNotBlank(searchParam.getOrder())){
            // 用户使用了排序按钮，记录用户的排序规则，返回给页面
            String[] split = searchParam.getOrder().split(":");

            String fieldFlag = split[0];
            String sortOrder = split[1];

            Map<String,String> orderMap = new HashMap<>();
            orderMap.put("sort",sortOrder);
            orderMap.put("type",fieldFlag);
            model.addAttribute("orderMap",orderMap);
        }

        // 面包屑
        if(StringUtils.isNotBlank(searchParam.getTrademark())){
            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }

        String[] props = searchParam.getProps();
        if(null!=props&&props.length>0){
            List<SearchAttr> searchAttrs = new ArrayList<>();
            // 循环props参数封装面包屑
            for (String prop : props) {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttr.setAttrName(prop.split(":")[2]);
                searchAttrs.add(searchAttr);
            }
            model.addAttribute("propsParamList",searchAttrs);
        }


        return "list/index";
    }

    private String makeUrlParam(SearchParam searchParam) {

        String urlParam = "http://list.gmall.com:8300/search.html?";

        String trademark = searchParam.getTrademark();
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();
        String order = searchParam.getOrder();

        if(null!=category3Id&&category3Id>0){
            urlParam += "category3Id="+category3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            urlParam += "keyword="+keyword;
        }

        if(StringUtils.isNotBlank(trademark)){
            urlParam += "&trademark="+trademark;
        }
        
        if(null!=props&&props.length>0){
            for (String prop : props) {
                urlParam += "&props="+prop;
            }
        }

        return urlParam;
    }


}
