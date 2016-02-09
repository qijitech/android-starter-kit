/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import android.text.TextUtils;
import com.smartydroid.android.starter.kit.account.AccountProvider;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.utilities.AppInfo;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultHeaderInterceptor implements HeaderInterceptor {

  AccountProvider mAccountProvider;
  ApiVersion mApiVersion;

  public DefaultHeaderInterceptor(AccountProvider accountProvider, ApiVersion apiVersion) {
    mAccountProvider = accountProvider;
    mApiVersion = apiVersion;
  }



  @Override public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Headers.Builder builder = new Headers.Builder();

    final AppInfo appInfo = StarterKitApp.appInfo();

    builder.add("Content-Encoding", "gzip")
        .add("version-code", appInfo.versionCode)
        .add("version-name", appInfo.version)
        .add("device", appInfo.deviceId)
        .add("platform", "android");

    final String channel = appInfo.channel;
    if (! TextUtils.isEmpty(channel)) {
      builder.add("channel", channel);
    }

    if (mAccountProvider != null && mAccountProvider.provideToken() != null) {
      builder.add("Authorization", "Bearer " + mAccountProvider.provideToken());
    }

    if (mApiVersion != null && mApiVersion.accept() != null) {
      builder.add("Accept", mApiVersion.accept());
    }

    Request compressedRequest = originalRequest
        .newBuilder()
        .headers(builder.build())
        .build();

    return chain.proceed(compressedRequest);
  }
}
