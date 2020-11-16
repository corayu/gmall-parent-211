package com.atguigu.gmall.all.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestIoJs {

    public static void main(String[] args) throws IOException {

        // 查询销售属性数据
        String spuJson = "{\"17|20\":\"14\",\"15|21\":\"15\"}";

        // 将销售属性生成为静态的json文件
        File file = new File("d:/spu_3.json");

        FileOutputStream fos = new FileOutputStream(file);

        fos.write(spuJson.getBytes());

    }
}
