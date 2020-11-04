package com.atguigu.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@MapperScan("com.atguigu.gmall.product.mapper")
@EnableSwagger2
public class ProductApplication {
	public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
        System.out.println("\033[35;5m" + "=================separator===============" + "\033[0m");
	}
}