package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.service.ListApiService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListApiServiceImpl implements ListApiService {

    @Autowired
    GoodsRepository elasticsearchRepository;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws IOException {

        // dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        //query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", "荣耀");
        boolQueryBuilder.must(matchQueryBuilder);
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", 61);
        boolQueryBuilder.filter(termQueryBuilder);


        // nested
        BoolQueryBuilder attrsBool = new BoolQueryBuilder();
        attrsBool.filter(new TermQueryBuilder("attrs.attrValue", "8GB"));
        attrsBool.filter(new TermQueryBuilder("attrs.attrId", 3));
        attrsBool.must(new MatchQueryBuilder("attrs.attrValue", "8GB"));
        NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", attrsBool, ScoreMode.None);
        boolQueryBuilder.filter(nestedQueryBuilder);

        // 将bool放入语句
        searchSourceBuilder.query(boolQueryBuilder);

        System.out.println("========" + searchSourceBuilder.toString() + "============");

        // 请求命令对象得封装
        String[] indeces = {"goods"};
        SearchRequest searchRequest = new SearchRequest(indeces, searchSourceBuilder);
        //SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    }


    /**
     * 搜索方法
     * @param searchParam
     * @return
     */
    @Override
    public SearchResponseVo list(SearchParam searchParam) {

        // 拼接dsl的封装
        SearchRequest searchRequest = buildQueryDsl(searchParam);

        // 执行dsl查询命令
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 封装vo对象
        SearchResponseVo searchResponseVo = parseSearchResult(search);
        return searchResponseVo;
    }

    /***
     * 解析搜索返回结果
     * @param search
     * @return
     */
    private SearchResponseVo parseSearchResult(SearchResponse search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        // 解析返回结果

        // 商品
        List<Goods> goods = new ArrayList<>();
        SearchHits hits = search.getHits();
        SearchHit[] resultHits = hits.getHits();
        if (null != resultHits && resultHits.length > 0) {
            for (SearchHit resultHit : resultHits) {
                String sourceAsString = resultHit.getSourceAsString();
                Goods good = JSON.parseObject(sourceAsString, Goods.class);

                // 解析高亮
                Map<String, HighlightField> highlightFields = resultHit.getHighlightFields();
                if(null!=highlightFields&&highlightFields.size()>0){
                    HighlightField title = highlightFields.get("title");
                    String titleName = title.getFragments()[0].toString();
                    good.setTitle(titleName);
                }

                goods.add(good);
            }
        }

        // 聚合函数
        Map<String, Aggregation> stringAggregationMap = search.getAggregations().asMap();
        // 商标聚合解析
        ParsedLongTerms tmIdAggParsedLongTerms = (ParsedLongTerms)stringAggregationMap.get("tmIdAgg");
        //Aggregation tmIdAgg = stringAggregationMap.get("tmIdAgg");
        // 获得商标聚合的bucket
        List<? extends Terms.Bucket> tmBuckets = tmIdAggParsedLongTerms.getBuckets();
        List<SearchResponseTmVo> trademarkList = tmBuckets.stream().map(bucket->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            String keyAsString = bucket.getKeyAsString();//tmId
            // 跟解析tmId的聚合一样，在进行一次聚合循环，拿到tmName
            Map<String, Aggregation> tmIdSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) tmIdSubMap.get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();

            // 跟解析tmId的聚合一样，在进行一次聚合循环，拿到tmLogoUrl
            Map<String, Aggregation> tmLogoUrlSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) tmLogoUrlSubMap.get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();

            searchResponseTmVo.setTmId(Long.parseLong(keyAsString));
            searchResponseTmVo.setTmName(tmName);
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            return searchResponseTmVo;
        }).collect(Collectors.toList());

        // 属性聚合解析
        ParsedNested attrAgg = (ParsedNested) stringAggregationMap.get("attrsAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdBuckets = attrIdAgg.getBuckets();

        List<SearchResponseAttrVo> searchResponseAttrVos = attrIdBuckets.stream().map(attrIdBucket->{
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            long attrId = attrIdBucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);

            Map<String, Aggregation> attrIdSubMap = attrIdBucket.getAggregations().asMap();
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) attrIdSubMap.get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);

            Map<String, Aggregation> attrValueSubMap = attrIdBucket.getAggregations().asMap();
            ParsedStringTerms attrValueAgg = (ParsedStringTerms) attrValueSubMap.get("attrValueAgg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(attrValueBucket->{
                String attrValue = attrValueBucket.getKeyAsString();
                return attrValue;
            }).collect(Collectors.toList());
            searchResponseAttrVo.setAttrValueList(attrValues);// 封装属性值的解析结果给属性Vo集合
            return searchResponseAttrVo;
        }).collect(Collectors.toList());




        searchResponseVo.setAttrsList(searchResponseAttrVos);
        searchResponseVo.setGoodsList(goods);
        searchResponseVo.setTrademarkList(trademarkList);
        return searchResponseVo;
    }

    /***
     * 构建dsl语句
     * @param searchParam
     * @return
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {

        String[] indeces = {"goods"};
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 分类
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();
        if (null != category3Id && category3Id > 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            boolQueryBuilder.filter(termQueryBuilder);

        }

        // 关键字
        String keyword = searchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }


        // 属性集合
        // 属性id:属性值名称:属性名称
        String[] props = searchParam.getProps();
        if(null!=props&&props.length>0){
            for (String prop : props) {
                String[] split = prop.split(":");
                String attrId = split[0];
                String attrValue = split[1];
                String attrName = split[2];

                // nested的属性
                BoolQueryBuilder attrsBool = new BoolQueryBuilder();
                attrsBool.filter(new TermQueryBuilder("attrs.attrId", attrId));
                attrsBool.filter(new TermQueryBuilder("attrs.attrValue", attrValue));
                attrsBool.must(new MatchQueryBuilder("attrs.attrName", attrName));
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", attrsBool, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }


        // 商标
        String trademark = searchParam.getTrademark();
        if (StringUtils.isNotBlank(trademark)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId", trademark.split(":")[0]);
            boolQueryBuilder.filter(termQueryBuilder);
        }


        searchSourceBuilder.query(boolQueryBuilder);

        // 聚合商标结果
        TermsAggregationBuilder tmTermsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(tmTermsAggregationBuilder);

        // 聚合属性结果
        NestedAggregationBuilder attrNestedAggregationBuilder = AggregationBuilders.nested("attrsAgg", "attrs").subAggregation(
                AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))
        );
        searchSourceBuilder.aggregation(attrNestedAggregationBuilder);

        // 分页,第一页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        // 高亮
        if(StringUtils.isNotBlank(keyword)){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        // 排序
        String order = searchParam.getOrder();
        if(StringUtils.isNotBlank(order)){
            String[] split = order.split(":");
            String fieldFlag = split[0];
            String sortOrder = split[1];

            String field = "hotScore";
            if(fieldFlag.equals("2")){
                field = "price";
            }
            searchSourceBuilder.sort(field, sortOrder.equals("asc")?SortOrder.ASC:SortOrder.DESC);
        }

        // 打印dsl语句
        SearchRequest searchRequest = new SearchRequest(indeces, searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        return searchRequest;
    }

    @Override
    public void upperGoods(Long skuId) {

        // 查询skuInfo
        Goods goods = new Goods();

        //查询sku对应的平台属性
        List<SearchAttr> searchAttrs = productFeignClient.getAttrList(skuId);
        goods.setAttrs(searchAttrs);

        //查询sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 查询品牌
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        if (baseTrademark != null) {
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());

        }

        // 查询分类
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        if (baseCategoryView != null) {
            goods.setCategory1Id(baseCategoryView.getCategory1Id());
            goods.setCategory1Name(baseCategoryView.getCategory1Name());
            goods.setCategory2Id(baseCategoryView.getCategory2Id());
            goods.setCategory2Name(baseCategoryView.getCategory2Name());
            goods.setCategory3Id(baseCategoryView.getCategory3Id());
            goods.setCategory3Name(baseCategoryView.getCategory3Name());
        }

        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setId(skuInfo.getId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setCreateTime(new Date());

        elasticsearchRepository.save(goods);

    }

    @Override
    public void lowerGoods(Long skuId) {

        elasticsearchRepository.deleteById(skuId);

    }

    @Override
    public void incrHotScore(Long skuId) {
        // 更新redis，返回当前分数
        Double aDouble = redisTemplate.opsForZSet().incrementScore("hotScore", skuId, 1);

        // 用当前分数摸10，如果没有余数，则更新es
        if (aDouble % 10 == 0) {
            // 调用es更新分数
            Optional<Goods> optional = elasticsearchRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(aDouble.longValue());
            elasticsearchRepository.save(goods);

        }

    }

}
