package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    ProductFeignClient procuFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void addToCart(Long skuId, Long skuNum, String userId) {

        SkuInfo skuInfo = procuFeignClient.getSkuInfo(skuId);

        CartInfo cartInfo = new CartInfo();

        cartInfo.setSkuPrice(skuInfo.getPrice());

        cartInfo.setIsChecked(1);

        cartInfo.setUserId(userId);

        cartInfo.setSkuName(skuInfo.getSkuName());

        cartInfo.setSkuId(skuId);

        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());

        cartInfo.setSkuNum(Integer.parseInt(skuNum + ""));

        cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(skuNum)));

        // db
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sku_id",skuId);
        CartInfo cartInfoExists = cartInfoMapper.selectOne(queryWrapper);

        if(null==cartInfoExists){
            cartInfoMapper.insert(cartInfo);
        }else {
            cartInfoExists.setCartPrice((cartInfoExists.getCartPrice().divide(new BigDecimal(cartInfoExists.getSkuNum()))).multiply(new BigDecimal(cartInfoExists.getSkuNum())));
            cartInfoExists.setSkuNum(cartInfoExists.getSkuNum()+Integer.parseInt(skuNum+""));
            cartInfoMapper.updateById(cartInfoExists);
        }

        // 将购物车信息放入缓存
        redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX).put(skuId+"",cartInfo);

    }

    @Override
    public void mergeToCartList(String userId, String userTempId) {

        // 正式用户购物车数据
        QueryWrapper<CartInfo> cartInfoQueryWrapperUserId = new QueryWrapper<>();
        cartInfoQueryWrapperUserId.eq("user_id", userId);
        List<CartInfo> cartInfosUser = cartInfoMapper.selectList(cartInfoQueryWrapperUserId);

        // 临时用户购物车数据
        QueryWrapper<CartInfo> cartInfoQueryWrapperTempId = new QueryWrapper<>();
        cartInfoQueryWrapperTempId.eq("user_id", userTempId);
        List<CartInfo> cartInfosTempUser = cartInfoMapper.selectList(cartInfoQueryWrapperTempId);

        if(null==cartInfosUser){
            if(null!=cartInfosTempUser){
                for (CartInfo cartInfo : cartInfosTempUser) {
                    cartInfoMapper.insert(cartInfo);
                }
            }
        }else{
            for (CartInfo cartInfo : cartInfosTempUser) {
                Long skuId = cartInfo.getSkuId();
                cartInfo.setUserId(userId);

                for (CartInfo info : cartInfosUser) {
                    Long skuIdFor = info.getSkuId();
                    if (skuId.equals(skuIdFor)) {
                        cartInfo.setSkuNum(cartInfo.getSkuNum() + info.getSkuNum());
                        cartInfo.setCartPrice(cartInfo.getCartPrice().add(info.getCartPrice()));// 注意，数据库中少一个skuPrice的字段
                        cartInfo.setIsChecked(info.getIsChecked());
                        cartInfo.setId(info.getId());// 在更新之前需要保证是正式主键，不是临时主键
                        // cartInfoMapper.updateById(cartInfo);
                    }else {
                        cartInfo.setUserId(userId);
                        //cartInfoMapper.insert(cartInfo);
                    }
                    // 插入最新的合并结果
                    cartInfoMapper.updateById(cartInfo);
                }
            }
        }
    }

    @Override
    public void deleteCartList(String userTempId) {
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userTempId);
        cartInfoMapper.delete(queryWrapper);

        // 清理缓存中的临时用户购物车数据
        redisTemplate.delete(RedisConst.USER_KEY_PREFIX+userTempId+RedisConst.USER_CART_KEY_SUFFIX);
    }

    @Override
    public List<CartInfo> checkIfMergeToCartList(String userTempId) {
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userTempId);
        List<CartInfo> cartInfos = cartInfoMapper.selectList(queryWrapper);
        return  cartInfos;
    }

    @Override
    public List<CartInfo>  loadCartCache(String userId) {

        // 从数据库中查出购物车列表
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<CartInfo> cartInfos = cartInfoMapper.selectList(queryWrapper);

        // 将购物车信息同步到缓存
        Map<String,CartInfo> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfos) {
            map.put(cartInfo.getSkuId()+"",cartInfo);
        }
        redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX).putAll(map);

        return cartInfos;
    }

    @Override
    public List<CartInfo> getCartList(String userId) {

        List<CartInfo> cartInfos = new ArrayList<>();

        // 先查缓存
        cartInfos = (List<CartInfo>)redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX).values();
        if(null==cartInfos||cartInfos.size()==0) {
            // 缓存没有查数据库
            cartInfos = loadCartCache(userId);
        }
        
        if(null!=cartInfos&&cartInfos.size()>0){
            for (CartInfo cartInfo : cartInfos) {
                SkuInfo skuInfo = procuFeignClient.getSkuInfo(cartInfo.getSkuId());
                cartInfo.setSkuPrice(skuInfo.getPrice());
            }
        }
        
        
        return cartInfos;
    }

    @Override
    public void checkCart(Long skuId, String isChecked,String userId) {

        // 更新数据库
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sku_id",skuId);

        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(Integer.parseInt(isChecked));
        cartInfoMapper.update(cartInfo,queryWrapper);

        // 更新缓存

        CartInfo cartInfoFromCache = (CartInfo)redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX).get(skuId+"");
        cartInfoFromCache.setIsChecked(Integer.parseInt(isChecked));
        redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX).put(skuId+"",cartInfo);
    }

    /**
     * 生成tradeNo
     * @param tradeNo
     * @param userId
     */
    @Override
    public void genTradeNo(String tradeNo, String userId) {

        redisTemplate.opsForValue().set(RedisConst.USER_KEY_PREFIX+userId+":tradeNo",tradeNo);

    }
}
