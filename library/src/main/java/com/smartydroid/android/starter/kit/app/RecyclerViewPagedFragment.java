/**
 * Created by YuGang Yang on November 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.recyclerview.PagedRecyclerViewHandler;
import com.smartydroid.android.starter.kit.recyclerview.ViewHandler;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;

public abstract class RecyclerViewPagedFragment<E extends Entitiy>
    extends RecyclerViewFragment<E>
    implements ViewHandler.OnScrollBottomListener,
    ILoadMoreView.OnLoadMoreClickListener {

  @Override public ViewHandler buildViewHandler() {
    return new PagedRecyclerViewHandler(getContext());
  }

  @Override public void onScorllBootom() {
    if (getPagePaginator().canLoadMore()) {
      getPagePaginator().loadMore();
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mViewHandler.setOnScrollBottomListener(mRecyclerView, this);
    mViewHandler.setOnLoadMoreClickListener(this);
  }

  @Override public void onLoadMoreClick(View view) {
    if (checkPaginator()) {
      getPagePaginator().loadMore();
    }
  }

  @Override public void endRequest() {
    super.endRequest();

    if (checkPaginator() && !getPagePaginator().hasMorePages() && checkViewHandler()) {
      mViewHandler.getLoadMoreView().showNoMore();
    }
  }

  @Override public void startRequest() {
    super.startRequest();
    if (checkPaginator() && !getPagePaginator().isRefresh() && checkViewHandler()) {
      mViewHandler.getLoadMoreView().showLoading();
    }
  }

  private boolean checkPaginator() {
    return getPagePaginator() != null;
  }

  private boolean checkViewHandler() {
    return mViewHandler != null && mViewHandler.getLoadMoreView() != null;
  }
}
