/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class StarterNetwork {

  private static final String sBaseUrl = "http://duanzi.net/";
  private Retrofit mRetrofit;

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    private static final StarterNetwork INSTANCE = new StarterNetwork();
  }

  public static synchronized StarterNetwork get() {
    return SingletonHolder.INSTANCE;
  }


  protected Retrofit.Builder newRetrofitBuilder() {
    return new Retrofit.Builder();
  }

  private Retrofit retrofit() {
    if (mRetrofit == null) {
      Retrofit.Builder builder = newRetrofitBuilder();
      mRetrofit = builder.baseUrl(sBaseUrl)
          .addConverterFactory(JacksonConverterFactory.create())
          .build();
    }

    return mRetrofit;
  }

  public static FeedService createFeedService() {
    return get().retrofit().create(FeedService.class);
  }
}
