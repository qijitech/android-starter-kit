package support.ui.utilities.debounced;

import android.view.View;

public interface DebouncedListener {
  boolean onDebouncedClick(View view, int position);
  boolean onDebouncedClick(View view);
}
