/**
 * Created by YuGang Yang on September 26, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import com.smartydroid.android.starter.kit.utilities.AppInfo;

public class StarterKitApp extends Application {

  private static volatile Context sAppContext;
  private static volatile StarterKitApp mInstance;
  private static volatile Handler sAppHandler;
  private static volatile AppInfo mAppInfo;

  @Override public void onCreate() {
    super.onCreate();

    initialize();
  }

  private void initialize() {
    mInstance = this;
    sAppContext = getApplicationContext();
    sAppHandler = new Handler(sAppContext.getMainLooper());
  }

  /**
   *
   * @return applicaton info
   */
  public static AppInfo getAppInfo() {
    if (mAppInfo == null) {
      mAppInfo = new AppInfo(appContext());
    }
    return getInstance().mAppInfo;
  }

  /**
   * @return application context
   */
  public static Context appContext() {
    return sAppContext;
  }

  /**
   * @return application resource
   */
  public static Resources appResources() {
    return appContext().getResources();
  }

  /**
   * @return application handler
   */
  public static Handler appHandler() {
    return sAppHandler;
  }

  /**
   * @return current application instance
   */
  public static StarterKitApp getInstance() {
    return mInstance;
  }

  @Override public void onTerminate() {
    super.onTerminate();
    sAppContext = null;
    mInstance = null;
    sAppHandler = null;
  }
}
