/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.widget;

import android.view.View;
import android.widget.ProgressBar;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

public class LoadMoreView implements ILoadMoreView {

  private View mContainer;
  private View mContainerContent;
  private ProgressBar mProgress;

  @Override
  public void init(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener) {
    mContainer = footViewHolder.addFootView(R.layout.include_loadmore_view);
    mProgress = ViewUtils.getView(mContainer, android.R.id.secondaryProgress);
    mContainerContent = ViewUtils.getView(mContainer, R.id.container_loadmore_content);
  }

  @Override public void showLoading() {
    ViewUtils.setGone(mProgress, false);
    ViewUtils.setGone(mContainerContent, true);
  }

  @Override public void showNoMore() {
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mContainerContent, false);
  }

  @Override public void showNormal() {
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mContainerContent, false);
  }

  @Override public void showFailure(Throwable t) {
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mContainerContent, false);
  }
}
