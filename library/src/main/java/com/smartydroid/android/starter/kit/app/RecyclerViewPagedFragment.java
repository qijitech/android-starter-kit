/**
 * Created by YuGang Yang on November 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.view.View;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.smartydroid.android.starter.kit.StarterKit;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;

public abstract class RecyclerViewPagedFragment<E extends Entity>
    extends RecyclerViewFragment<E>
    implements
    ILoadMoreView.OnLoadMoreClickListener {

  @Override public Paginate buildPaginate() {
    return Paginate.with(getRecyclerView(), this)
        .setLoadingTriggerThreshold(StarterKit.getLoadingTriggerThreshold())
        .addLoadingListItem(true)
        .setLoadingListItemCreator(null)
        .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
          @Override
          public int getSpanSize() {
            return 3;
          }
        })
        .build();
  }

  @Override public void onLoadMore() {
    getPagePaginator().loadMore();
  }

  /**
   * load more click
   * @param view View
   */
  @Override public void onLoadMoreClick(View view) {
    if (checkPaginator()) {
      getPagePaginator().loadMore();
    }
  }

  @Override public void endRequest() {
    super.endRequest();

    if (checkPaginator() && !getPagePaginator().hasMorePages()) {
      //mViewHandler.getLoadMoreView().showNoMore();
      // set no more
      // TODO
      getAdapter().notifyDataSetChanged();
    }
  }

  @Override public void startRequest() {
    super.startRequest();
    if (checkPaginator() && !getPagePaginator().isRefresh()) {
      //mViewHandler.getLoadMoreView().showLoading();
      // set show loading
      // TODO
    }
  }

  private boolean checkPaginator() {
    return getPagePaginator() != null;
  }

}
