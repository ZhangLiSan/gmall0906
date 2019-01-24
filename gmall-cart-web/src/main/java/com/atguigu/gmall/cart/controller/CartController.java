package com.atguigu.gmall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    CartService cartService;

    @Reference
    SkuService skuService;

    @LoginRequired(isNeedLogin = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request,
                          HttpServletResponse response,
                          ModelMap map){

        return "toTrade";
    }

    @LoginRequired(isNeedLogin = false)
    @RequestMapping("checkCart")
    public String checkCart(CartInfo cartInfo,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            ModelMap map){
        //远程http的rest风格的ws的调用代码
        String userId = "2";
        List<CartInfo> cartList = new ArrayList<>();
        if(StringUtils.isNotBlank(userId)){
            cartList= cartService.cartListFromCache(userId);
        } else {
            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            cartList = JSON.parseArray(listCartCookie, CartInfo.class);
        }

        //修改购物车的状态
        for (CartInfo info : cartList) {
            if(info.getSkuId().equals(cartInfo.getSkuId())){
                info.setIsChecked(cartInfo.getIsChecked());
                if(StringUtils.isNotBlank(userId)){
                    cartService.updateCart(info);
                    //刷新缓存
                    cartService.flushCartCacheByUser(userId);
                }else{
                    //覆盖cookie
                    CookieUtil.setCookie(request,response,
                            "cartListCookies",
                            JSON.toJSONString(cartList),
                            1000 * 60 * 60 * 24,
                            true);
                }

            }
            map.put("cartList",cartList);
//            BigDecimal b = getMySum
        }


        return "cartListInner";
    }














}
