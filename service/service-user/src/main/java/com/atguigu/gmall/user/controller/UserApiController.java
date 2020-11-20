package com.atguigu.gmall.user.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("api/user/passport")
public class UserApiController {

    @Autowired
    UserService userService;

    @RequestMapping("login")
    Result login(@RequestBody UserInfo userInfo){
        String  token = userService.login(userInfo);
        if(StringUtils.isEmpty(token)){
            return Result.ok(ResultCodeEnum.LOGIN_AUTH);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", userInfo.getName());
        map.put("nickName", userInfo.getNickName());
        map.put("token", token);

        return Result.ok(map);
    }


    @RequestMapping("inner/getUserId/{token}")
    String getUserId(@PathVariable("token") String token){

        String userId = userService.getUserId(token);

        return userId;
    }
}
