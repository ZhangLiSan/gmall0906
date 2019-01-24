package com.atguigu.gmall.item.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ItemController {

    @Reference
    SpuService spuService;

    @Reference
    SkuService skuService;


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId") String skuId, ModelMap map, HttpServletRequest request){
        SkuInfo skuInfo = skuService.item(skuId,request.getRemoteAddr());
        String spuId = skuInfo.getSpuId();

        // 销售属性的集合
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = new ArrayList<>();

        spuSaleAttrListCheckBySku = spuService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        map.put("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);

        map.put("skuInfo",skuInfo);

        // 隐藏一个hash表
        List<SkuInfo> skuInfos = skuService.getSkuSaleAttrValueListBySpu(spuId);

        Map<String,String> skuMap = new HashMap<>();
        for (SkuInfo sku : skuInfos) {
            String v = sku.getId();

            List<SkuSaleAttrValue> skuSaleAttrValues = sku.getSkuSaleAttrValueList();

            String k = "";
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
                String valueId = skuSaleAttrValue.getSaleAttrValueId();

                k = k + "|"+valueId;
            }

            skuMap.put(k,v);
        }

        map.put("skuMap", JSON.toJSONString(skuMap));

        return "item";
    }

    @RequestMapping("index")
    public String index(ModelMap map){

        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("元素"+i);
            list2.add("成员"+i);
        }

        map.put("list",list);
        map.put("list2",list2);
        map.put("hello","hello thymeleaf !");

        List<UserInfo> userInfos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setName("小"+i);
            userInfos.add(userInfo);
        }
        map.put("userInfos",null);

        UserInfo userInfo = new UserInfo();
        userInfo.setName("老王");
        map.put("userInfo",null);
        return "index";
    }


}
