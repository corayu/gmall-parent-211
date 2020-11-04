package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.test.FdfsTest;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class FileUploadController {

    @RequestMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws Exception {
        String imgUrl = "http://192.168.200.128:8080";

        // 读取配置
        String path = FdfsTest.class.getClassLoader().getResource("tracker.conf").getPath();
        System.out.println(path);

        // 创建tracker连接
        ClientGlobal.init(path);

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();

        // 获得storage连接
        StorageClient storageClient = new StorageClient(connection,null);


        // 通过storage上传文件
        String originalFilename = file.getOriginalFilename();
//        int i = originalFilename.lastIndexOf(".");
//        String substring = originalFilename.substring(i);
        String extension = FilenameUtils.getExtension(originalFilename);
        String[] jpgs = storageClient.upload_file(file.getBytes(), extension, null);


        for (String jpg : jpgs) {
            imgUrl = imgUrl + "/" + jpg;
        }

        System.out.println(imgUrl);
        return Result.ok(imgUrl);

    }

}