package com.smartydroid.android.kit.demo.util;

import android.app.Activity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.smartydroid.android.starter.kit.R;

/**
 * Created by YuGang Yang on February 27, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public final class DialogUtils {

  private DialogUtils() {
  }

  public static void showLoginDialog(Activity activity) {
    showLoginDialog(activity, null, null);
  }

  public static void showLoginDialog(Activity activity,
      MaterialDialog.SingleButtonCallback onPositiveCallback,
      MaterialDialog.SingleButtonCallback onNegativeCallback) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
        .title(R.string.starter_dialog_login_title)
        .content(R.string.starter_dialog_login_content)
        .positiveText(R.string.starter_dialog_login_positive)
        .negativeText(R.string.starter_dialog_login_negative);
    if (onPositiveCallback != null) {
      builder.onPositive(onPositiveCallback);
    }
    if (onNegativeCallback != null) {
      builder.onNegative(onNegativeCallback);
    }
    builder.show();
  }
}
