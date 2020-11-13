package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
