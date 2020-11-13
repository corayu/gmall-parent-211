package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ListFeignClient feignListClient;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        // 保存sku基本信息
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();

        // 保存sku图片集合
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }

        // 保存sku对应平台属性集合
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        // 保存sku对应销售属性集合
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
    }

    @Override
    public IPage<SkuInfo> list(Page pageParam) {

        IPage iPage = skuInfoMapper.selectPage(pageParam, null);

        return iPage;
    }

    @Override
    public void onSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);

        // 将来要调用es插入已经上架的商品
        feignListClient.upperGoods(skuId);
    }

    @Override
    public void cancelSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);

        // 将来要调用es删除已经下架的商品
        feignListClient.lowerGoods(skuId);

    }


    @Override
    @GmallCache
    public SkuInfo getSkuInfoNx(Long skuId) {
        SkuInfo skuInfo = getSkuInfoFromDb(skuId);
        return skuInfo;
    }

    public SkuInfo getSkuInfoNxBak(Long skuId) {
        SkuInfo skuInfo = null;
        String skuKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
        // 查询缓存
        String skuCache = (String) redisTemplate.opsForValue().get(skuKey);
        if (StringUtils.isNotBlank(skuCache)) {
            skuInfo = JSON.parseObject(skuCache, SkuInfo.class);
        } else {
            // 查询db时必须获得分布式锁，以保证数据库操作的安全性
            String uid = UUID.randomUUID().toString();
            Boolean stockLock = redisTemplate.opsForValue().setIfAbsent("sku:"+skuId+":lock", uid, 1, TimeUnit.SECONDS);//3秒钟分布式锁过期时间
            if (stockLock) {
                skuInfo = getSkuInfoFromDb(skuId);
                // 数据库查询完成，放入redis缓存
                if (null!=skuInfo) {
                    redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(skuInfo));
                } else {
                    // 访问不存在的key，防止空对象到redis中，防止缓存穿透
                    redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(new SkuInfo()), 10, TimeUnit.SECONDS);
                }
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                // redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);
                redisTemplate.execute(redisScript, Arrays.asList("sku:"+skuId+":lock"), uid);
            } else {
                // 自选
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfoNx(skuId);
            }
        }
        return skuInfo;
    }


    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = null;
        String skuKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;

        //查询缓存
        String skuCache = (String) redisTemplate.opsForValue().get(skuKey);

        if (StringUtils.isNotBlank(skuCache)) {
            skuInfo = JSON.parseObject(skuCache, SkuInfo.class);
        } else {
            skuInfo = getSkuInfoFromDb(skuId);
            //数据库查询完成,放入redis缓存
            if (null!=skuInfo) {
                redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(skuInfo));
            }
        }
        return skuInfo;
    }

    private SkuInfo getSkuInfoFromDb(Long skuId) {
        SkuInfo skuInfo = null;
        //如果缓存没有,查询数据库
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", skuId);
        skuInfo = skuInfoMapper.selectOne(queryWrapper);
        if (null!=skuInfo) {
            QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
            skuImageQueryWrapper.eq("sku_id", skuId);
            List<SkuImage> skuImages = skuImageMapper.selectList(skuImageQueryWrapper);
            skuInfo.setSkuImageList(skuImages);
        }

        return skuInfo;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", skuId);
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    @Override
    public List<Map<String, Object>> getSkuValueIdsMap(Long spuId) {


        List<Map<String, Object>> map = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        return map;
    }
}
