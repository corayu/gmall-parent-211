package com.atguigu.gmall.user.client;

import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/passport/inner/getUserId/{token}")
    String getUserId(@PathVariable("token") String token);

    @RequestMapping("api/user/passport/inner/getUserAddresses/{userId}")
    List<UserAddress> getUserAddresses(@PathVariable("userId") String userId);
}
