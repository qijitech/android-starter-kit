/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.carlosdelachica.easyrecycleradapters.decorations.DividerItemDecoration;
import com.paginate.Paginate;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.contracts.Pagination.PaginatorContract;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.smartydroid.android.starter.kit.widget.LoadingLayout;
import java.util.ArrayList;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public abstract class RecyclerViewFragment<E extends Entity>
    extends CallbackFragment<ArrayList<E>>
    implements LoadingLayout.OnButtonClickListener,
    Paginate.Callbacks,
    EasyViewHolder.OnItemClickListener,
    EasyViewHolder.OnItemLongClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    PaginatorCallback<E> {

  LoadingLayout mLoadingLayout;
  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  // network
  PaginatorContract<E> mPagePaginator;

  // new
  private EasyRecyclerAdapter mAdapter;
  private Paginate mPaginate;

  public abstract void bindViewHolders(EasyRecyclerAdapter adapter);

  public abstract PaginatorContract<E> buildPaginator();

  public void viewHolderFactory(EasyRecyclerAdapter adapter) {
    // Left blank
  }

  public void setupLoadingLayout(LoadingLayout layout) {
    // Left blank
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPagePaginator = buildPaginator();
    mAdapter = new EasyRecyclerAdapter(getContext());
    viewHolderFactory(mAdapter);
    mAdapter.setOnClickListener(this);
    mAdapter.setOnLongClickListener(this);
    bindViewHolders(mAdapter);
  }

  public PaginatorContract<E> getPagePaginator() {
    return mPagePaginator;
  }

  public Paginate getPaginate() {
    return mPaginate;
  }

  @Override protected int getFragmentLayout() {
    return R.layout.content_recycler_view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mLoadingLayout = ViewUtils.getView(view, R.id.container_loading_layout);
    mSwipeRefreshLayout = mLoadingLayout.getContentView();
    mRecyclerView = ViewUtils.getView(mSwipeRefreshLayout, android.R.id.list);
    mSwipeRefreshLayout.setOnRefreshListener(this);

    buildRecyclerView();
    setupEmptyView();
    setupLoadingLayout(mLoadingLayout);
  }

  @Override public void onResume() {
    super.onResume();
    updateView();
    refresh();
  }

  @Override public void onPause() {
    super.onPause();
    if (mPagePaginator.isLoading()) {
      mPagePaginator.cancel();
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mLoadingLayout = null;
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPagePaginator = null;
  }

  public void buildRecyclerView() {
    // set layout manager
    mRecyclerView.setLayoutManager(buildLayoutManager());
    // add item decoration
    mRecyclerView.addItemDecoration(buildItemDecoration());

    mRecyclerView.setItemAnimator(new SlideInUpAnimator());

    // set adapter
    mRecyclerView.setAdapter(mAdapter);

    // build paginate
    mPaginate = buildPaginate();
  }

  public Paginate buildPaginate() {
    return Paginate.with(mRecyclerView, this).addLoadingListItem(false).build();
  }

  public RecyclerView.LayoutManager buildLayoutManager() {
    return new LinearLayoutManager(getContext());
  }

  public RecyclerView.ItemDecoration buildItemDecoration() {
    DividerItemDecoration decoration = new DividerItemDecoration(getContext());
    decoration.setInsets(buildInsets());
    final int dividerRes = buildDivider();
    if (dividerRes > 0) {
      decoration.setDivider(dividerRes);
    }
    return decoration;
  }

  public @DrawableRes int buildDivider() {
    return 0;
  }

  public @DimenRes int buildInsets() {
    return R.dimen.starter_divider_insets;
  }

  public RecyclerView getRecyclerView() {
    return mRecyclerView;
  }

  public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  private void updateView() {
    if (! viewValid(mLoadingLayout)) {
      return;
    }
    if (!isEmpty()) {
      mLoadingLayout.showContentView();
    } else if (!mPagePaginator.dataHasLoaded()) {
      mLoadingLayout.showLoadingView();
    } else if (mPagePaginator.hasError()) {
      mLoadingLayout.showErrorView();
    } else {
      mLoadingLayout.showEmptyView();
    }
}

  public boolean isEmpty() {
    return mPagePaginator.isEmpty();
  }

  @Override public void onRefresh() {
    if (!mPagePaginator.isLoading()) {
      mPagePaginator.refresh();
    } else {
      mSwipeRefreshLayout.setRefreshing(false);
    }
  }

  public void setupEmptyView() {
    mLoadingLayout.setOnButtonClickListener(this);
  }

  @Override public void onEmptyButtonClick(View view) {
    mPagePaginator.refresh();
  }

  @Override public void onErrorButtonClick(View view) {
    mPagePaginator.refresh();
  }

  public E getItem(int position) {
    if (mPagePaginator == null || mPagePaginator.isEmpty()) {
      return null;
    }
    return mPagePaginator.items().get(position);
  }

  @Override public void onItemClick(int position, View view) {
  }
  @Override public boolean onLongItemClicked(int position, View view) {
    return false;
  }

  ///////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////
  @Override public void startRequest() {
    if (isEmpty() && viewValid(mLoadingLayout)) {
      mLoadingLayout.showLoadingView();
    }
  }

  @Override public void endRequest() {
    if (mSwipeRefreshLayout != null && mPagePaginator.isRefresh()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    updateView();
  }

  @Override public void respondSuccess(ArrayList<E> data) {
    if (mAdapter != null) {
      mAdapter.addAll(mPagePaginator.items());
    }
  }

  public void refresh() {
    if (mPagePaginator != null && !mPagePaginator.isLoading()) {
      mPagePaginator.refresh();
    }
  }

  public void setupError(Drawable drawable, String title, String subtitle) {
    if (viewValid(mLoadingLayout)) {
      if (drawable != null) {
        mLoadingLayout.setErrorDrawable(drawable);
      }
      mLoadingLayout.setErrorTitle(title);
      mLoadingLayout.setErrorSubtitle(subtitle);
    }
  }

  public void setupEmpty(Drawable drawable, String title, String subtitle) {
    if (viewValid(mLoadingLayout)) {
      if (drawable != null) {
        mLoadingLayout.setEmptyDrawable(drawable);
      }
      mLoadingLayout.setEmptyTitle(title);
      mLoadingLayout.setEmptySubtitle(subtitle);
    }
  }

  public LoadingLayout loadingLayout() {
    return mLoadingLayout;
  }

  @Override public void onLoadMore() {
  }

  @Override public boolean isLoading() {
    return mPagePaginator != null && mPagePaginator.isLoading();
  }

  @Override public boolean hasLoadedAllItems() {
    return mPagePaginator != null && !mPagePaginator.canLoadMore() && !mPagePaginator.hasError();
  }
}
