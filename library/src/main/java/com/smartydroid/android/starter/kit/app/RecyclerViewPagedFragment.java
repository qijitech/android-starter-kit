/**
 * Created by YuGang Yang on November 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.StarterKit;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public abstract class RecyclerViewPagedFragment<E extends Entity> extends RecyclerViewFragment<E> {

  @Override public Paginate buildPaginate() {
    return Paginate.with(getRecyclerView(), this)
        .setLoadingTriggerThreshold(StarterKit.getLoadingTriggerThreshold())
        .addLoadingListItem(true)
        .setLoadingListItemCreator(new CustomLoadingListItemCreator())
        .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
          @Override public int getSpanSize() {
            return 3;
          }
        })
        .build();
  }

  @Override public void onLoadMore() {
    if (getPagePaginator().canLoadMore()) {
      getPagePaginator().loadMore();
    }
  }

  /**
   * load more click
   *
   * @param view View
   */
  private void onLoadMoreClick(View view) {
    if (checkPaginator()) {
      getPagePaginator().loadMore();
    }
  }

  @Override public void endRequest() {
    super.endRequest();
    getPaginate().setHasMoreDataToLoad(getPagePaginator().hasMorePages());
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

  private class CustomLoadingListItemCreator
      implements LoadingListItemCreator, View.OnClickListener {
    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      View view = inflater.inflate(R.layout.list_item_loading, parent, false);
      return new VH(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      VH vh = (VH) holder;

      if (mPagePaginator.hasError()) {
        ViewUtils.setGone(vh.progressBar, true);
        ViewUtils.setGone(vh.textLoading, false);
        vh.textLoading.setText(R.string.starter_loadingmore_failure);
        vh.itemView.setOnClickListener(this);
      } else {
        ViewUtils.setGone(vh.progressBar, false);
        ViewUtils.setGone(vh.textLoading, true);
      }

      // This is how you can make full span if you are using StaggeredGridLayoutManager
      if (getRecyclerView().getLayoutManager() instanceof StaggeredGridLayoutManager) {
        StaggeredGridLayoutManager.LayoutParams params =
            (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
        params.setFullSpan(true);
      }
    }

    @Override public void onClick(View v) {
      onLoadMoreClick(v);
    }
  }

  static class VH extends RecyclerView.ViewHolder {
    TextView textLoading;
    CircularProgressBar progressBar;

    public VH(View itemView) {
      super(itemView);
      textLoading = ViewUtils.getView(itemView, R.id.text_loading);
      progressBar = ViewUtils.getView(itemView, R.id.circular_progress_bar);
    }
  }
}
