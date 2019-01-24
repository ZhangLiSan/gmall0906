package com.atguigu.gmall.service;


import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {


    List<CartInfo> cartListFromCache(String userId);

    void updateCart(CartInfo info);

    void flushCartCacheByUser(String userId);
}
