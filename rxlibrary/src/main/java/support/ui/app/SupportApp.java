package support.ui.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import me.alexrs.prefs.lib.Prefs;
import support.ui.utilities.AppInfo;

/**
 * Created by YuGang Yang on 04 07, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class SupportApp extends Application {

  private static volatile Context sAppContext;
  private static volatile SupportApp mInstance;
  private static volatile Handler sAppHandler;
  private static volatile AppInfo mAppInfo;

  @Override public void onCreate() {
    super.onCreate();
    initialize();
  }

  @Override public void onTerminate() {
    super.onTerminate();
    sAppContext = null;
    mInstance = null;
    sAppHandler = null;
    mAppInfo = null;
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
   * @return Resource dimension value multiplied by the appropriate metric.
   */
  public static float dimen(@DimenRes int dimenRes) {
    return appResources().getDimension(dimenRes);
  }

  public static int color(@ColorRes int colorRes) {
    return ContextCompat.getColor(appContext(), colorRes);
  }

  public static Drawable drawable(@DrawableRes int drawableRes) {
    return ContextCompat.getDrawable(appContext(), drawableRes);
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
  public static SupportApp getInstance() {
    return mInstance;
  }

  private void initialize() {
    mInstance = this;
    sAppContext = getApplicationContext();
    sAppHandler = new Handler(sAppContext.getMainLooper());
  }

  public static void enterApp() {
    Prefs.with(appContext()).save("is_first_enter_app", appInfo().versionCode);
  }

  /**
   * 判断是否第一次进入APP
   * @return
   */
  public static boolean isFirstEnterApp() {
    String ver = Prefs.with(appContext()).getString("is_first_enter_app", null);
    if (TextUtils.isEmpty(ver) || !ver.equals(appInfo().versionCode)) {
      return true;
    } else {
      return false;
    }
  }
}
