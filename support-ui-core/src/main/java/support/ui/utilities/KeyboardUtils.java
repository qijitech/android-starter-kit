/**
 * Created by YuGang Yang on October 31, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package support.ui.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public final class KeyboardUtils {

  private static InputMethodManager get(Context context) {
    return (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
  }

  // 打开关闭切换
  public static void show(Context context) {
    try {
      InputMethodManager manager = get(context);
      manager.toggleSoftInput(0, HIDE_NOT_ALWAYS);
    } catch (Exception e) {
      // Nothing
    }
  }

  public static void show(Context context, View view) {
    InputMethodManager manager = get(context);
    manager.showSoftInput(view, 0);
  }

  public static void setSoftInputMode(Activity activity, int flag) {
    activity.getWindow().setSoftInputMode(flag);
  }

  public static void hide(Context context, @Nullable IBinder binder) {
    InputMethodManager manager = get(context);
    if (manager.isActive()) {
      manager.hideSoftInputFromWindow(binder, 0);
    }
  }

  public static boolean isActive(Context context, View view) {
    InputMethodManager manager = get(context);
    return manager.isActive(view);
  }

  public static boolean isActive(Context context) {
    InputMethodManager manager = get(context);
    return manager.isActive();
  }

  private KeyboardUtils() {

  }
}
