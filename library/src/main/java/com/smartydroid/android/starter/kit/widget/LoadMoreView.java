/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.widget;

import android.view.View;
import com.smartydroid.android.starter.kit.R;

public class LoadMoreView implements ILoadMoreView {

  @Override
  public void init(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener) {
    footViewHolder.addFootView(R.layout.include_loading_view);
  }

  @Override public void showLoading() {

  }

  @Override public void showNoMore() {

  }

  @Override public void showNormal() {

  }

  @Override public void showFailure(Exception e) {

  }
}
