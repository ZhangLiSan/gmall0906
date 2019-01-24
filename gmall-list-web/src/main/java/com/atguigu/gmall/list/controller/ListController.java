package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.ListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class ListController {

    @Reference
    ListService listService;

    @Reference
    AttrService attrService;

    @RequestMapping("list.html")
    public String list(SkuLsParam skuLsParam, ModelMap map){

        // 调用list查询服务
        List<SkuLsInfo> skuLsInfoList = listService.list(skuLsParam);
        Set<String> valueIds = new HashSet<>();
        for (SkuLsInfo skuLsInfo : skuLsInfoList) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                valueIds.add(valueId);
            }
        }

        // 查询检索结果列表中包含的分类属性集合
        String join = StringUtils.join(valueIds, ",");
        List<BaseAttrInfo> attrList = new ArrayList<>();
        attrList = attrService.getAttrListByValueIds(join);
        map.put("skuLsInfoList",skuLsInfoList);

        // 制作面包屑
//        List<AtrrValueCrumb> atrrValueCrumbs = new ArrayList<>();
//        String[] crumbValueIds = skuLsParam.getValueId();
//        if(crumbValueIds!=null&&crumbValueIds.length>0){
//            for (String crumbValueId : crumbValueIds) {
//                AtrrValueCrumb atrrValueCrumb = new AtrrValueCrumb();
//
//                for (BaseAttrInfo baseAttrInfo : attrList) {
//                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
//                    for (BaseAttrValue baseAttrValue : attrValueList) {
//                        String id = baseAttrValue.getId();
//                        if(id.equals(crumbValueId)){
//                            String valueName = baseAttrValue.getValueName();
//                            atrrValueCrumb.setValueName(valueName);
//                        }
//                    }
//                }
//
//
//                String myCrumbUrlParam = getMyCrumbUrlParam(skuLsParam, crumbValueId);
//                atrrValueCrumb.setUrlParam(myCrumbUrlParam);// url是当前请求参数减去面包屑valueId
//                atrrValueCrumbs.add(atrrValueCrumb);
//            }
//        }


        // 去掉已经提交的属性
        String[] valueId = skuLsParam.getValueId();
        List<AtrrValueCrumb> atrrValueCrumbs = new ArrayList<>();
        if(valueId!=null&&valueId.length>0){
            Iterator<BaseAttrInfo> iterator = attrList.iterator();

            while (iterator.hasNext()) {
                List<BaseAttrValue> attrValueList = iterator.next().getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    // 列表中的属性值id
                    String id = baseAttrValue.getId();
                    for (String sid : valueId) {
                        // 如果sid与id匹配，则去掉id所在的属性

                        if(id.equals(sid)){
                            // 有一个属性值被去掉，就有一个面包屑诞生
                            AtrrValueCrumb atrrValueCrumb = new AtrrValueCrumb();
                            String myCrumbUrlParam = getMyCrumbUrlParam(skuLsParam, sid);
                            atrrValueCrumb.setUrlParam(myCrumbUrlParam);
                            atrrValueCrumb.setValueName(baseAttrValue.getValueName());
                            atrrValueCrumbs.add(atrrValueCrumb);
                            iterator.remove();
                        }
                    }
                }
            }

        }
        map.put("attrValueSelectedList",atrrValueCrumbs);
        map.put("attrList",attrList);

        // 根据当前请求参数对象，生成当前请求参数字符串
        String urlParam = getMyUrlParam(skuLsParam);
        map.put("urlParam",urlParam);


        return "list";
    }

    private String getMyCrumbUrlParam(SkuLsParam skuLsParam, String... crumbValueId) {

        String urlParam = "";
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" +catalog3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" +keyword;
        }

        if(valueId!=null&&valueId.length>0){
            for (String id : valueId) {
                if(crumbValueId!=null&&!crumbValueId[0].equals(id)){
                    urlParam = urlParam + "&valueId=" +id;
                }
            }
        }

        return urlParam;
    }


    private String getMyUrlParam(SkuLsParam skuLsParam) {
        String urlParam = "";
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" +catalog3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" +keyword;
        }

        if(valueId!=null&&valueId.length>0){
            for (String id : valueId) {
                urlParam = urlParam + "&valueId=" +id;
            }
        }

        return urlParam;
    }
}
