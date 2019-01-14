package com.atguigu.gmall.manage.testFdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;

public class Test01 {

    public static void main(String[] args) throws IOException, MyException {
        ClientGlobal.init("tracker.conf");

        // 创建tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();
        // 创建storage
        StorageClient storageClient = new StorageClient(connection,null);

        // 上传文件
        String[] gifs = storageClient.upload_file("D:\\a.gif", "gif", null);

        for (String gif : gifs) {
            System.out.println(gif);
        }
    }
}
