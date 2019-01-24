package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuInfo;

import java.util.List;

public interface SkuService {


    SkuInfo item(String skuId,String ip) ;

    List<SkuInfo> skuInfoListBySpu(String spuId);

    List<SkuInfo> getSkuSaleAttrValueListBySpu(String skuId);

    SkuInfo itemFromDb(String skuId);

    void saveSku(SkuInfo skuInfo);

    List<SkuInfo> SkuListByCatalog3Id(String catalog3Id);
}
