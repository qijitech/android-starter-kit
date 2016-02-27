package com.smartydroid.android.starter.kit.utilities;

import android.content.Context;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by YuGang Yang on February 27, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public final class HudUtils {

  public static KProgressHUD showHud(Context context, String label) {
    return showHud(context, label, false);
  }

  public static KProgressHUD showHud(Context context, String label, boolean isCancellable) {
    return KProgressHUD.create(context)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(label)
        .setCancellable(isCancellable)
        .setDimAmount(0.5F)
        .show();
  }
}
