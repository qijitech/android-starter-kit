/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import timber.log.Timber;

public class RetrofitBuilder {

  private String baseUrl;
  private Retrofit mRetrofit;
  private HttpLoggingInterceptor.Level level;

  private DefaultHeaderInterceptor mHeaderInterceptor;
  private HttpLoggingInterceptor mLoggingInterceptor;

  private RetrofitBuilder() {
    mHeaderInterceptor = new DefaultHeaderInterceptor(AccountManager.getInstance(), StarterKitApp.getInstance());
    mLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
      @Override public void log(String message) {
        Timber.d(message);
      }
    });
    mLoggingInterceptor.setLevel(level);
  }

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    private static final RetrofitBuilder INSTANCE = new RetrofitBuilder();
  }

  public static synchronized RetrofitBuilder get() {
    return SingletonHolder.INSTANCE;
  }

  protected Retrofit.Builder newRetrofitBuilder() {
    return new Retrofit.Builder();
  }

  public Retrofit retrofit() {
    if (baseUrl == null) {
      Utils.checkNotNull(baseUrl, "Base URL required.");
    }

    if (mRetrofit == null) {
      Retrofit.Builder builder = newRetrofitBuilder();

      OkHttpClient client = new OkHttpClient();

      client.interceptors().add(mHeaderInterceptor);
      client.interceptors().add(mLoggingInterceptor);

      mRetrofit = builder.baseUrl(baseUrl)
          .addConverterFactory(JacksonConverterFactory.create())
          .client(client)
          .build();
    }

    return mRetrofit;
  }

  /**
   * 创建给定的 Api Service
   */
  public <T> T create(Class<T> clazz) {
    return get().retrofit().create(clazz);
  }

  public static class Builder {
    private String baseUrl;
    private HttpLoggingInterceptor.Level level;

    public RetrofitBuilder build() {
      if (baseUrl == null) {
        Utils.checkNotNull(baseUrl, "Base URL required.");
      }

      ensureSaneDefaults();

      RetrofitBuilder retrofitBuilder = get();
      retrofitBuilder.baseUrl = baseUrl;
      retrofitBuilder.level = level;
      return retrofitBuilder;
    }

    private void ensureSaneDefaults() {
      if (level == null) {
        level = HttpLoggingInterceptor.Level.NONE;
      }
    }

    public Builder baseUrl(String baseUrl) {
      Utils.checkNotNull(baseUrl, "baseUrl == null");
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder debug(boolean debug) {
      level = debug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
      return this;
    }
  }
}
