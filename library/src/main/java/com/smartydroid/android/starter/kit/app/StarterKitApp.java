/**
 * Created by YuGang Yang on September 26, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class StarterKitApp extends Application {

  private static Context sAppContext;
  private static StarterKitApp mInstance;

  @Override public void onCreate() {
    super.onCreate();
    mInstance = this;
    sAppContext = getApplicationContext();
  }

  /**
   * @return application context
   */
  public static Context getAppContext() {
    return sAppContext;
  }

  /**
   * @return application resource
   */
  public static Resources getAppResources() {
    return getAppContext().getResources();
  }

  /**
   * @return current application instance
   */
  public static StarterKitApp getInstance() {
    return mInstance;
  }
}
