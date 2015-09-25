/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.smartydroid.android.starter.kit.R;

public class ResourceLoadMoreIndicator extends FrameLayout {

  public ResourceLoadMoreIndicator(Context context) {
    this(context, null);
  }

  public ResourceLoadMoreIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ResourceLoadMoreIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context);
  }

  private void initialize(Context context) {
    LayoutInflater.from(context).inflate(R.layout.include_loadmore_view, this, true);
  }
}
