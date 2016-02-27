/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.utilities.Preconditions;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

public class RetrofitBuilder {

  private String baseUrl;
  private Retrofit mRetrofit;

  private OkHttpClient client;

  private RetrofitBuilder() {
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
    Preconditions.checkNotNull(baseUrl, "Base URL required.");

    if (mRetrofit == null) {
      Retrofit.Builder builder = newRetrofitBuilder();

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
    private String accept;
    private OkHttpClient mClient;

    public RetrofitBuilder build() {
      Preconditions.checkNotNull(baseUrl, "Base URL required.");
      Preconditions.checkNotNull(accept, "Api Version required");

      ensureSaneDefaults();

      RetrofitBuilder retrofitBuilder = get();
      retrofitBuilder.baseUrl = baseUrl;
      retrofitBuilder.client = mClient;

      return retrofitBuilder;
    }

    private void ensureSaneDefaults() {
      if (mClient == null) {
        mClient = defaultClient();
      }
    }

    private OkHttpClient defaultClient() {
      // default interceptors
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
          new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
              Timber.d(message);
            }
          });
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(loggingInterceptor);
      builder.addInterceptor(new DefaultHeaderInterceptor(AccountManager.getInstance(), accept));
      return builder.build();
    }

    public Builder client(OkHttpClient client) {
      mClient = client;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      Preconditions.checkNotNull(baseUrl, "baseUrl == null");
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder accept(String accept) {
      Preconditions.checkNotNull(accept, "accept == null");
      this.accept = accept;
      return this;
    }
  }
}
