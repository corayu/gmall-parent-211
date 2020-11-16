package com.atguigu.gmall.all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.atguigu.gmall"})
public class WebAllApplication {

public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class, args);System.out.println("\033[35;5m"+"=====================separator======================"+"\033[0m");
    }

}
