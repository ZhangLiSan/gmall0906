package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttrServiceImpl implements AttrService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {

        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        return baseAttrValues;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(String join) {

       List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectAttListByValueIds(join);

       return baseAttrInfos ;

    }


    @Override
    public void updateAttr(BaseAttrInfo baseAttrInfo) {

//        baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
//        String attrId = baseAttrInfo.getId();
//        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
//
//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            baseAttrValue.setAttrId(attrId);
//            baseAttrValueMapper.updateByPrimaryKeySelective(baseAttrValue);
//        }
        String attrId = baseAttrInfo.getId();
        //如果有主键就进行更新
        if(attrId!=null&&attrId.length()>0){
            //执行更新
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        }else{
            //没有主键
            //判断主键是否为空
            if(attrId!=null&&attrId.length()==0){
                //主键为空，设置为null
                baseAttrInfo.setId(null);
            }
            //执行插入
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        //清空baseattrvalue中的值
        BaseAttrValue attrValue = new BaseAttrValue();
        attrValue.setAttrId(attrId);
        baseAttrValueMapper.delete(attrValue);
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if(attrValueList!=null&&attrValueList.size()>0){
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //判断主键是否为空
                if(baseAttrValue.getId()!=null&&baseAttrValue.getId().length()==0){
                    baseAttrValue.setId(null);
                }
                baseAttrValue.setAttrId(attrId);
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }

    }

    @Override
    public List<BaseCatalog1> getCatalog1() {

        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {

        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> baseCatalog2s = baseCatalog2Mapper.select(baseCatalog2);

        return baseCatalog2s;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {

        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> baseCatalog3s = baseCatalog3Mapper.select(baseCatalog3);

        return baseCatalog3s;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);

        //　平台属性集合
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);

        for (BaseAttrInfo attrInfo : baseAttrInfos) {
            List<BaseAttrValue> baseAttrValues = new ArrayList<>();

            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrInfo.getId());

           // 平台属性值集合
            baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
            attrInfo.setAttrValueList(baseAttrValues);
        }

        return baseAttrInfos;
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insertSelective(baseAttrInfo);
        
        //baseAttrInfo 插入后数据主键返回
        String attrId = baseAttrInfo.getId();

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(attrId);
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }
    }

    @Override
    public void removeAttr(String attrId) {

        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> attrValueList = baseAttrValueMapper.select(baseAttrValue);
        for (BaseAttrValue attrValue : attrValueList) {
            baseAttrValueMapper.delete(attrValue);

        }

        baseAttrInfoMapper.deleteByPrimaryKey(attrId);
    }
}
