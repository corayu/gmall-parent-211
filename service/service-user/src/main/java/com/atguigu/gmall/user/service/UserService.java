package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

public interface UserService {

    String getUserId(String token);

    String login(UserInfo userInfo);
}
