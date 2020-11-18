package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter {


    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 拿到请求的request和response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 拿到当前请求的url
        URI uri = request.getURI();
        String path = request.getURI().getPath();

        // 不拦截的请求，如css，js，图片等
        if(path.lastIndexOf(".png")!=-1||path.lastIndexOf(".jpg")!=-1||path.lastIndexOf(".css")!=-1||path.lastIndexOf(".js")!=-1){
            return chain.filter(exchange);
        }


        // 校验内部服务调用的url:api/...
        boolean match = antPathMatcher.match("/**/inner/**", path);
        if(match){
            return out(response,ResultCodeEnum.PERMISSION);
        }

        // 校验白名单(web系统的请求)
        

        return chain.filter(exchange);
    }

    /***
     * 过滤器打印结果给页面
     * @param response
     * @param resultCodeEnum
     * @return
     */
    private Mono<Void> out(ServerHttpResponse response,ResultCodeEnum resultCodeEnum) {

        // 讲返回结果内容封装给response
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);

        // 设置response返回内容的编码格式
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输出到页面
        return response.writeWith(Mono.just(wrap));
    }

}
