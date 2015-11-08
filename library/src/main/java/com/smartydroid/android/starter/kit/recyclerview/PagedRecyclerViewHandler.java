/**
 * Created by YuGang Yang on November 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.recyclerview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.starter.kit.adapter.HFAdapter;
import com.smartydroid.android.starter.kit.adapter.HFRecyclerAdapter;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;
import com.smartydroid.android.starter.kit.widget.LoadMoreView;
import java.util.List;

public class PagedRecyclerViewHandler implements ViewHandler {

  private final Context context;
  private EasyRecyclerAdapter mAdapter;
  private HFAdapter mWrapperAdapter;
  private ILoadMoreView mLoadMoreView;
  private RecyclerViewOnScrollListener mScrollListener;

  public PagedRecyclerViewHandler(Context context) {
    this.context = context;
  }

  @Override public void onCreate() {
    mAdapter = new EasyRecyclerAdapter(context);
    mWrapperAdapter = new HFRecyclerAdapter(mAdapter);
    mLoadMoreView = new LoadMoreView();
    mLoadMoreView.initialize(new FooterViewAdder(mWrapperAdapter));
  }

  @Override public void onDestroyView(RecyclerView recyclerView) {
    recyclerView.removeOnScrollListener(mScrollListener);
  }

  @Override public void onDestroy() {
    mLoadMoreView.onDestroy();
    mScrollListener = null;
    mAdapter = null;
    mWrapperAdapter = null;
    mLoadMoreView = null;
  }

  @Override public void setupAdapter(RecyclerView recyclerView) {
    recyclerView.setAdapter(mWrapperAdapter);
  }

  @Override public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  @Override public void notifyDataSetChanged(List<?> items) {
    mAdapter.addAll(items);
  }

  @Override public ILoadMoreView getLoadMoreView() {
    return mLoadMoreView;
  }

  @Override
  public void setOnScrollBottomListener(RecyclerView recyclerView, OnScrollBottomListener l) {
    mScrollListener = new RecyclerViewOnScrollListener(l);
    recyclerView.addOnScrollListener(mScrollListener);
  }

  @Override public void setOnLoadMoreClickListener(ILoadMoreView.OnLoadMoreClickListener l) {
    mLoadMoreView.setOnLoadMoreClickListener(l);
  }

  private static class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    private ViewHandler.OnScrollBottomListener onScrollBottomListener;

    public RecyclerViewOnScrollListener(OnScrollBottomListener onScrollBottomListener) {
      super();
      this.onScrollBottomListener = onScrollBottomListener;
    }

    @Override public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView,
        int newState) {
      if (newState == RecyclerView.SCROLL_STATE_IDLE && isScollBottom(recyclerView)) {
        if (onScrollBottomListener != null) {
          onScrollBottomListener.onScorllBootom();
        }
      }
    }

    private boolean isScollBottom(RecyclerView recyclerView) {
      return !isCanScollVertically(recyclerView);
    }

    private boolean isCanScollVertically(RecyclerView recyclerView) {
      if (android.os.Build.VERSION.SDK_INT < 14) {
        return ViewCompat.canScrollVertically(recyclerView, 1)
            || recyclerView.getScrollY() < recyclerView.getHeight();
      } else {
        return ViewCompat.canScrollVertically(recyclerView, 1);
      }
    }

    @Override
    public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
    }
  }

  private static class FooterViewAdder implements ILoadMoreView.FootViewAdder {

    private HFAdapter mWrapperAdapter;

    public FooterViewAdder(HFAdapter wrapperAdapter) {
      mWrapperAdapter = wrapperAdapter;
    }

    @Override public View addFootView(View footerView) {
      mWrapperAdapter.addFooter(footerView);
      return footerView;
    }

    @Override public View addFootView(int layoutId) {
      final Context context = StarterKitApp.appContext();
      View view = LayoutInflater.from(context).inflate(layoutId, null, false);
      return addFootView(view);
    }
  }
}
