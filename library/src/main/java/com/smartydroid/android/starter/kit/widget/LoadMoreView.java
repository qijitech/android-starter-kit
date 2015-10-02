/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.widget;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

public class LoadMoreView implements ILoadMoreView {

  private View mContainer;
  private TextView mTextView;
  private ProgressBar mProgress;

  @Override
  public void init(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener) {
    mContainer = footViewHolder.addFootView(R.layout.include_loadmore_view);

    if (onClickLoadMoreListener != null) {
      mContainer.setOnClickListener(onClickLoadMoreListener);
    }

    mProgress = ViewUtils.getView(mContainer, android.R.id.progress);
    mTextView = ViewUtils.getView(mContainer, android.R.id.text1);
  }

  @Override public void showLoading() {
    ViewUtils.setGone(mProgress, false);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore);
  }

  @Override public void showNoMore() {
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore_nomore);
  }

  @Override public void showNormal() {
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore_more);
  }

  @Override public void showFailure(Throwable t) {
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore_more);
  }
}
