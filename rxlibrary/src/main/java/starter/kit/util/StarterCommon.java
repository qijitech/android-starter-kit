package starter.kit.util;

import android.app.Activity;
import support.ui.utilities.KeyboardUtils;

import static starter.kit.util.Preconditions.checkNotNull;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class StarterCommon {

  private Activity activity;

  public static StarterCommon create(Activity activity) {
    return new StarterCommon(activity);
  }

  private StarterCommon(Activity activity) {
    checkNotNull(activity, "activity == null");
    this.activity = activity;
  }

  public void onDestroy() {
    activity = null;
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
