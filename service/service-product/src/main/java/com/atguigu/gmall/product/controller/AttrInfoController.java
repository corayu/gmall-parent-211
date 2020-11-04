package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class AttrInfoController {

    @Autowired
    AttrInfoService attrInfoService;

    @RequestMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){

        attrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();

    }


    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") String category1Id, @PathVariable("category2Id") String category2Id, @PathVariable("category3Id") String category3Id){

        List<BaseAttrInfo> baseAttrInfos = attrInfoService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfos);
    }
}
