package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter {

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    UserFeignClient userFeignClient;

    @Value("${authUrls.url}")
    String authUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 拿到请求的request和response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 拿到当前请求的url
        URI uri = request.getURI();
        String path = request.getURI().getPath();

        // 不拦截的请求，如css，js，图片等
        if (path.lastIndexOf("login")!=-1 || path.lastIndexOf(".png")!=-1 || path.lastIndexOf(".jpg")!=-1 || path.lastIndexOf(".css")!=-1 || path.lastIndexOf(".js")!=-1) {
            return chain.filter(exchange);
        }


        // 校验内部服务调用的url:api/...
        boolean matchInner = antPathMatcher.match("/**/inner/**", path);
        if (matchInner) {
            return out(response, ResultCodeEnum.PERMISSION);
        }

        // 获取用户Id
        String userId = getUserId(request);
        boolean matchAuth = antPathMatcher.match("/**/auth/**", path);
        if (matchAuth) {
            if (StringUtils.isEmpty(userId)) {
                // 跳入登录页面
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originUrl="+request.getURI());
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }


        // 校验白名单(web系统的请求)
        String urii = request.getURI().toString();
        String[] split = authUrls.split(",");
        for (String url : split) {
            if (urii.indexOf(url)!=-1 && StringUtils.isEmpty(userId)) {
                // 跳入登录页面
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originUrl="+request.getURI());
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }

        // 如果用户已经正常通过token的认证校验
        if (!StringUtils.isEmpty(userId)) {
            request.mutate().header("userId", userId).build();
            chain.filter(exchange.mutate().request(request).build());
        }


        return chain.filter(exchange);
    }

    private String getUserId(ServerHttpRequest request) {

        String userId = "";
        String token = "";

        // 获得token
        MultiValueMap<String, HttpCookie> cookieMultiValueMap = request.getCookies();
        HttpCookie cookie = cookieMultiValueMap.getFirst("token");
        if (cookie!=null) {
            token = URLDecoder.decode(cookie.getValue());
        }

        if (!StringUtils.isEmpty(token)) {
            // 通过认证中心获得userId
            userId = userFeignClient.getUserId(token);
        }
        return userId;
    }

    /***
     * 过滤器打印结果给页面
     * @param response
     * @param resultCodeEnum
     * @return
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {

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
