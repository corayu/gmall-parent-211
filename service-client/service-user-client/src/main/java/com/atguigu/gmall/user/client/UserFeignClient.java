package com.atguigu.gmall.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/passport/inner/getUserId/{token}")
    String getUserId(@PathVariable("token") String token);
}
