package com.atguigu.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ManageUploadUtil {

    public static String imgUpolad(MultipartFile multipartFile) {

        String imgUrl = "http://192.168.222.20";// 常量类

        try {
            ClientGlobal.init("tracker.conf");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        // 创建tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = null;
        try {
            connection = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建storage
        StorageClient storageClient = new StorageClient(connection,null);

        // 上传文件
        String originalFilename = multipartFile.getOriginalFilename();//123.jpg
        int i = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(i+1);
        String[] gifs = new String[0];
        try {
            gifs = storageClient.upload_file(multipartFile.getBytes(), substring, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        for (String gif : gifs) {
            imgUrl = imgUrl +"/" + gif;
        }
        return imgUrl;
    }
}
