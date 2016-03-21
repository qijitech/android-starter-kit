package com.smartydroid.android.kit.demo.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.smartydroid.android.kit.demo.R;

public class AppEmptyView extends FrameLayout {

  public AppEmptyView(Context context) {
    super(context);
    initialize(context);
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.app_view_empty_view, this, false);
    addView(view);
  }
}
