/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.widget;

import android.view.View;

public interface ILoadMoreView {

  void initialize(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener);

  void hideLoading();

  /**
   * 显示正在加载中的布局
   */
  void showLoading();

  /**
   * 显示已经加载完成，没有更多数据的布局
   */
  void showNoMore();

  /**
   * 显示普通保布局
   */
  void showNormal();

  /**
   * 显示加载失败的布局
   */
  void showFailure(Throwable t);

  interface FootViewAdder {

    View addFootView(View view);

    View addFootView(int layoutId);
  }
}
