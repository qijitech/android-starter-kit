/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.utilities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;

public interface ViewHandler {

  boolean handleSetAdapter(View contentView, RecyclerView.Adapter<?> adapter,
      ILoadMoreView loadMoreView, View.OnClickListener onClickLoadMoreListener);

  void setOnScrollBottomListener(View contentView, OnScrollBottomListener onScrollBottomListener);

  interface OnScrollBottomListener {
    void onScorllBootom();
  }
}
