/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.utilities.Utils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;
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
    if (baseUrl == null) {
      Utils.checkNotNull(baseUrl, "Base URL required.");
    }

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

    private HeaderInterceptor mHeaderInterceptor;

    private HttpLoggingInterceptor.Level level;
    private HttpLoggingInterceptor mLoggingInterceptor;

    private final List<Interceptor> interceptors = new ArrayList<>();
    private final List<Interceptor> networkInterceptors = new ArrayList<>();

    private OkHttpClient mClient;

    public RetrofitBuilder build() {
      if (baseUrl == null) {
        Utils.checkNotNull(baseUrl, "Base URL required.");
      }

      ensureSaneDefaults();

      RetrofitBuilder retrofitBuilder = get();
      retrofitBuilder.baseUrl = baseUrl;
      retrofitBuilder.client = mClient;

      return retrofitBuilder;
    }

    private void ensureSaneDefaults() {
      if (level == null) {
        level = HttpLoggingInterceptor.Level.NONE;
      }

      if (mHeaderInterceptor == null) {
        mHeaderInterceptor = new DefaultHeaderInterceptor(AccountManager.getInstance(), StarterKitApp.getInstance());
      }

      if (mLoggingInterceptor == null) {
        mLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
          @Override public void log(String message) {
            Timber.d(message);
          }
        });
        mLoggingInterceptor.setLevel(level);
      }

      if (mClient == null) {
        // custom interceptors
        interceptors.add(mHeaderInterceptor);
        interceptors.add(mLoggingInterceptor);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().addAll(interceptors);
        builder.networkInterceptors().addAll(networkInterceptors);
        mClient = builder.build();
      }
    }

    public Builder client(OkHttpClient client) {
      mClient = client;
      return this;
    }

    public Builder headerInterceptor(HeaderInterceptor headerInterceptor) {
      mHeaderInterceptor = headerInterceptor;
      return this;
    }

    public Builder loggingInterceptor(HttpLoggingInterceptor loggingInterceptor) {
      mLoggingInterceptor = loggingInterceptor;
      return this;
    }

    public Builder addInterceptors(Interceptor interceptor) {
      interceptors.add(interceptor);
      return this;
    }

    public Builder addNetworkInterceptors(Interceptor interceptor) {
      networkInterceptors.add(interceptor);
      return this;
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
