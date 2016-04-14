/**
 * Created by YuGang Yang on September 26, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.util.Log;
import com.smartydroid.android.starter.kit.StarterKit;
import com.smartydroid.android.starter.kit.account.Account;
import com.smartydroid.android.starter.kit.utilities.FakeCrashLibrary;
import support.ui.app.SupportApp;
import timber.log.Timber;

public abstract class StarterKitApp extends SupportApp {

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
