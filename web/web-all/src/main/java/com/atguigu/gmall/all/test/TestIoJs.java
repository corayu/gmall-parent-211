package com.atguigu.gmall.all.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestIoJs {
    public static void main(String[] args) throws IOException {
        //查询销售属性数据
        String spuJson = "{\"10|12|13\":\"12\",\"10|11|13\":\"13\",\"9|11|13\":\"10\",\"9|12|13\":\"11\"}";
        
        //讲销售属性生成静态json文件
        File file = new File("d:/spu_3.json");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(spuJson.getBytes());
    }
}
