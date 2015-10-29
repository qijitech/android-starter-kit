/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class DefaultHeaderInterceptor implements Interceptor {

  @Override public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Request compressedRequest = originalRequest.newBuilder()
        .header("Content-Encoding", "gzip")
        .header("version-code", StarterKitApp.appInfo().versionCode)
        .header("version-name", StarterKitApp.appInfo().version)
        .header("device", StarterKitApp.appInfo().deviceId)
        .header("channel", StarterKitApp.appInfo().channel)
        .header("platform", "android")
        .header("Authorization", "Bearer {yourtokenhere}")
        .header("Accept", "application/vnd.financerapp.v1+json")
        .build();
    return chain.proceed(compressedRequest);
  }
}
