package support.ui.utilities;

import android.view.ViewGroup;

public final class LayoutHelper {

  public static final int MATCH_PARENT = -1;
  public static final int WRAP_CONTENT = -2;

  public static ViewGroup.LayoutParams createViewGroupLayoutParams() {
    return new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
  }

  private LayoutHelper() {
  }
}
