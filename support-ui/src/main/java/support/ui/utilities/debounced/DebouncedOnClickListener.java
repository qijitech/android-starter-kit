package support.ui.utilities.debounced;

import android.view.View;

public abstract class DebouncedOnClickListener implements View.OnClickListener, DebouncedListener {
  private final DebouncedClickHandler debouncedClickHandler;

  protected DebouncedOnClickListener() {
    this.debouncedClickHandler = new DebouncedClickHandler(this);
  }

  @Override public void onClick(View view) {
    debouncedClickHandler.invokeDebouncedClick(view);
  }
}
