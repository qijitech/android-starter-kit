package com.smartydroid.android.kit.demo.api.service;

import com.smartydroid.android.kit.demo.model.entity.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public interface AuthService {

  /**
   * 登录接口
   *
   * @param phone 手机号码
   * @param password 密码
   * @return Call
   */
  @FormUrlEncoded @POST("/auth/login") Call<User> login(
      @Field("phone") String phone,
      @Field("password") String password);

  @GET("/user/profile") Call<User> profile();
}
