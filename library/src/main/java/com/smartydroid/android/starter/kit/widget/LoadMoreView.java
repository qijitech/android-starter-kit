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
import java.lang.ref.WeakReference;

public class LoadMoreView implements ILoadMoreView {

  private View mContainer;
  private TextView mTextView;
  private ProgressBar mProgress;

  @Override public void initialize(FootViewAdder footViewHolder) {
    mContainer = footViewHolder.addFootView(R.layout.include_loadmore_view);
    mProgress = ViewUtils.getView(mContainer, android.R.id.progress);
    mTextView = ViewUtils.getView(mContainer, android.R.id.text1);
  }

  @Override public void onDestroy() {
  }

  @Override public void setOnLoadMoreClickListener(OnLoadMoreClickListener l) {
    if (mContainer != null && l != null) {
      mContainer.setOnClickListener(new MyClick(l));
    }
  }

  @Override public void hideLoading() {
    ViewUtils.setInvisible(mContainer, true);
  }

  @Override public void showLoading() {
    ViewUtils.setInvisible(mContainer, false);
    ViewUtils.setGone(mProgress, false);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore);
  }

  @Override public void showNoMore() {
    ViewUtils.setInvisible(mContainer, false);
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore_nomore);
  }

  @Override public void showNormal() {
    ViewUtils.setInvisible(mContainer, false);
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore_more);
  }

  @Override public void showFailure(Throwable t) {
    ViewUtils.setInvisible(mContainer, false);
    ViewUtils.setGone(mProgress, true);
    ViewUtils.setGone(mTextView, false);
    mTextView.setText(R.string.starter_loadingmore_failure);
  }

  private static class MyClick implements View.OnClickListener {

    private WeakReference<OnLoadMoreClickListener> mRefs;

    public MyClick(OnLoadMoreClickListener listener) {
      mRefs = new WeakReference<>(listener);
    }

    @Override public void onClick(View v) {
      if (mRefs != null && mRefs.get() != null) {
        final OnLoadMoreClickListener onLoadMoreClickListener = mRefs.get();
        onLoadMoreClickListener.onLoadMoreClick(v);
      }
    }
  }
}
