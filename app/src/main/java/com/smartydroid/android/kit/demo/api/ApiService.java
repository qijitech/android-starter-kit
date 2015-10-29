/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.api;

import com.smartydroid.android.kit.demo.api.service.NewsService;
import com.smartydroid.android.starter.kit.retrofit.RetrofitBuilder;
import retrofit.Retrofit;

public class ApiService {

  public static NewsService createNewsService() {
    return retrofit().create(NewsService.class);
  }

  private static Retrofit retrofit() {
    return RetrofitBuilder.get().retrofit();
  }
}
