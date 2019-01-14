package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {


    List<UserAddress> getAddressList();

    List<UserAddress> getAddressListByUserId(String userId);

    UserAddress getAddressListById(String addressId);

    List<UserInfo> getUserList();
}
