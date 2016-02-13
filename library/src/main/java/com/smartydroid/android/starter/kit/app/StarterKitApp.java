/**
 * Created by YuGang Yang on September 26, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import com.smartydroid.android.starter.kit.StarterKit;
import com.smartydroid.android.starter.kit.account.Account;
import com.smartydroid.android.starter.kit.retrofit.ApiVersion;
import com.smartydroid.android.starter.kit.utilities.AppInfo;
import com.smartydroid.android.starter.kit.utilities.FakeCrashLibrary;
import timber.log.Timber;

public abstract class StarterKitApp extends Application implements ApiVersion {

  private static volatile Context sAppContext;
  private static volatile StarterKitApp mInstance;
  private static volatile Handler sAppHandler;
  private static volatile AppInfo mAppInfo;

  /**
   * 根据 account json 返回 account
   *
   * @param json json value
   * @return Account
   */
  public abstract Account accountFromJson(String json);

  @Override public void onCreate() {
    super.onCreate();

    if (StarterKit.isDebug()) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new CrashReportingTree());
    }

    initialize();
  }

  private void initialize() {
    mInstance = this;
    sAppContext = getApplicationContext();
    sAppHandler = new Handler(sAppContext.getMainLooper());
  }

  /**
   * @return applicaton info
   */
  public static AppInfo appInfo() {
    if (mAppInfo == null) {
      mAppInfo = new AppInfo(appContext());
    }
    return mAppInfo;
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
    mAppInfo = null;
  }

  /** A tree which logs important information for crash reporting. */
  private static class CrashReportingTree extends Timber.Tree {
    @Override protected void log(int priority, String tag, String message, Throwable t) {
      if (priority == Log.VERBOSE || priority == Log.DEBUG) {
        return;
      }

      FakeCrashLibrary.log(priority, tag, message);

      if (t != null) {
        if (priority == Log.ERROR) {
          FakeCrashLibrary.logError(t);
        } else if (priority == Log.WARN) {
          FakeCrashLibrary.logWarning(t);
        }
      }
    }
  }
}
