package support.ui.adapters.debounced;

import android.os.SystemClock;
import android.view.View;

class DebouncedClickHandler {
  final static long MINIMUM_INTERVAL_MILLIS = 300;
  private final DebouncedListener debouncedOnClickListener;
  private long previousClickTimestamp;

  public DebouncedClickHandler(DebouncedListener debouncedOnClickListener) {
    this.debouncedOnClickListener = debouncedOnClickListener;
  }

  public boolean invokeDebouncedClick(int position, View view) {
    long currentTimestamp = SystemClock.uptimeMillis();
    boolean handled = false;
    if (currentTimestamp - previousClickTimestamp > MINIMUM_INTERVAL_MILLIS) {
      handled = debouncedOnClickListener.onDebouncedClick(view, position);
    }
    previousClickTimestamp = currentTimestamp;
    return handled;
  }
}
