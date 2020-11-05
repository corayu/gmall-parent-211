package com.atguigu.gmall.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.ApiListing;

@RestController
public class TestController {
    @RequestMapping("testLock")
    public String testLock() {
        return "剩余库存数量:0";
    }
}
