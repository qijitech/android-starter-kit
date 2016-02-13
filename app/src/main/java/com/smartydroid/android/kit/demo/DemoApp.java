/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.os.StrictMode;
import com.smartydroid.android.starter.kit.StarterKit;
import com.smartydroid.android.starter.kit.account.Account;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.retrofit.RetrofitBuilder;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

public class DemoApp extends StarterKitApp {

  @Override public void onCreate() {

    // common config
    new StarterKit.Builder()
        .setDebug(BuildConfig.DEBUG)
        .build();

    super.onCreate();
    //enabledStrictMode();
    //LeakCanary.install(this);

    // init api service
    new RetrofitBuilder.Builder()
        .debug(BuildConfig.DEBUG)
        .baseUrl(Profile.API_ENDPOINT)
        .build();
  }

  private void enabledStrictMode() {
    if (SDK_INT >= GINGERBREAD) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
          .detectAll() //
          .penaltyLog() //
          .penaltyDeath() //
          .build());
    }
  }

  @Override public Account accountFromJson(String json) {
    return null;
  }

  @Override public String accept() {
    return Profile.API_ACCEPT;
  }
}
