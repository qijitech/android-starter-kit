/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.utilities;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import com.smartydroid.android.starter.kit.adapter.HFAdapter;
import com.smartydroid.android.starter.kit.adapter.HFRecyclerAdapter;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;
import com.smartydroid.android.starter.kit.widget.ResourceLoadMoreIndicator;

public class RecyclerViewHandler implements ViewHandler {

  @Override public boolean handleSetAdapter(View contentView, RecyclerView.Adapter<?> adapter,
      ILoadMoreView loadMoreView, View.OnClickListener onClickLoadMoreListener) {
    final RecyclerView recyclerView = (RecyclerView) contentView;
    boolean hasInit = false;

    RecyclerView.Adapter<?> adapter2 = adapter;
    if (loadMoreView != null) {
      final HFAdapter hfAdapter = new HFRecyclerAdapter(adapter);

      adapter2 = hfAdapter;

      final Context context = recyclerView.getContext().getApplicationContext();
      loadMoreView.initialize(new ILoadMoreView.FootViewAdder() {
        @Override public View addFootView(View view) {
          hfAdapter.addFooter(view);
          return view;
        }

        @Override public View addFootView(int layoutId) {
          View view = LayoutInflater.from(context).inflate(layoutId, recyclerView, false);
          return addFootView(view);
        }
      }, onClickLoadMoreListener);
      hasInit = true;
    }

    recyclerView.setAdapter(adapter2);
    return hasInit;
  }

  @Override public void setOnScrollBottomListener(View contentView,
      OnScrollBottomListener onScrollBottomListener) {
    final RecyclerView recyclerView = (RecyclerView) contentView;
    recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(onScrollBottomListener));
  }

  /**
   * 滑动监听
   */
  private static class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    private OnScrollBottomListener onScrollBottomListener;

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
}
