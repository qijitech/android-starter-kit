/**
 * Created by YuGang Yang on October 31, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.utilities;

import android.app.Activity;
import com.kaopiz.kprogresshud.KProgressHUD;

import static com.smartydroid.android.starter.kit.utilities.Preconditions.checkNotNull;

public class StarterCommon {

  private KProgressHUD mHud;
  private Activity activity;

  public static StarterCommon create(Activity activity) {
    return new StarterCommon(activity);
  }

  private StarterCommon(Activity activity) {
    checkNotNull(activity, "activity == null");
    this.activity = activity;
  }

  public void onDestroy() {
    mHud = null;
    activity = null;
  }

  // hud
  public void showHud(int resId) {
    if (!isFinishing()) {
      showHud(activity.getString(resId));
    }
  }

  public void showHud(String text) {
    if (!isFinishing()) {
      showHud(text, false);
    }
  }

  public void showHud(String text, boolean isCancellable) {
    if (!isFinishing()) {
      mHud = HudUtils.showHud(activity, text, isCancellable);
    }
  }

  public void dismissHud() {
    if (mHud != null && !isFinishing()) {
      mHud.dismiss();
    }
  }

  // keyboard
  public void hideSoftInputMethod() {
    try {
      if (activity.getCurrentFocus() != null) {
        KeyboardUtils.hide(activity, activity.getCurrentFocus().getWindowToken());
      }
    } catch (Exception e) {
      // Nothing
    }
  }

  public void showSoftInputMethod() {
    try {
      KeyboardUtils.show(activity);
    } catch (Exception e) {
      // Nothing
    }
  }

  public boolean isImmActive() {
    return KeyboardUtils.isActive(activity);
  }

  private boolean isFinishing() {
    return activity == null || activity.isFinishing();
  }
}
