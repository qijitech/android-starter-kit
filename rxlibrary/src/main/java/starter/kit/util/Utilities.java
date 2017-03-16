package starter.kit.util;

import android.support.v7.widget.RecyclerView;

import static support.ui.utilities.Objects.isNotNull;

public final class Utilities {

  public static boolean isAdapterEmpty(RecyclerView.Adapter adapter) {
    return isNotNull(adapter) && adapter.getItemCount() <= 0;
  }
}
