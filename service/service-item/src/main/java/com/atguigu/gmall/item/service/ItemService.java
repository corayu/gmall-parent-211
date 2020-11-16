package com.atguigu.gmall.item.service;

import java.util.Map;

public interface ItemService {
    Map<String, Object> getItemThread(Long skuId);

    Map<String,Object> getItem(Long skuId);
}
