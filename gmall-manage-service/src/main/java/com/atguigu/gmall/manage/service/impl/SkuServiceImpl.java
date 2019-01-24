package com.atguigu.gmall.manage.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SkuImageMapper;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;




    @Override
    public List<SkuInfo> skuInfoListBySpu(String spuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);
        return skuInfos;
    }

    @Override
    public List<SkuInfo> getSkuSaleAttrValueListBySpu(String skuId) {

        List<SkuInfo> skuInfos = skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(skuId);
        return skuInfos;
    }


    @Override
    public SkuInfo item(String skuId,String ip) {

        System.out.println(ip+"访问"+skuId+"商品");

        SkuInfo skuInfo = null;

        // 从缓存中取出sku的数据
        Jedis jedis = redisUtil.getJedis();
        String skuInfoStr;
        skuInfoStr = jedis.get("sku:" + skuId + ":info");
        skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);


        // 从db中取出sku的数据
        if(skuInfo==null)//缓存中没有
        {
            System.out.println(ip+"发现缓存中没有"+skuId+"商品数据，申请分布式锁");
            // 拿到分布式锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10000);

            if(StringUtils.isBlank(OK)){
                System.out.println(ip+"分布式锁申请失败，三秒后开始自旋");

                // 缓存锁被占用，等一会儿继续申请
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return item(skuId,ip);
            }else{
                System.out.println(ip+"分布式锁申请成功，访问数据库");
                // 拿到缓存锁，可以访问数据库
                skuInfo = itemFromDb(skuId);
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(ip+"成功访问数据库后，归还锁，将"+skuId+"商品放入缓存");
            jedis.del("sku:"+skuId+":lock");
            // 同步缓存一份儿
            jedis.set("sku:"+skuId+":info",JSON.toJSONString(skuInfo));
        }

        jedis.close();
        return skuInfo;
    }


    @Override
    public SkuInfo itemFromDb(String skuId){

        // 从db中取出sku的数据
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo info = skuInfoMapper.selectOne(skuInfo);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);
        info.setSkuImageList(skuImages);
        return info;
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {

        //保存sku信息
        skuInfoMapper.insertSelective(skuInfo);
        String skuId = skuInfo.getId();

        //保存图片的信息
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insertSelective(skuImage);
        }

        // 保存销售属性信息
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }

        // 保存平台属性信息
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insertSelective(skuAttrValue);
        }

    }

    @Override
    public List<SkuInfo> SkuListByCatalog3Id(String catalog3Id) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : skuInfos) {
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(info.getId());
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
            info.setSkuAttrValueList(skuAttrValues);
        }

        return skuInfos;
    }


}
