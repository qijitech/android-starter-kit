/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

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
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import com.smartydroid.android.starter.kit.recyclerview.RecyclerViewHandler;
import com.smartydroid.android.starter.kit.recyclerview.ViewHandler;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.smartydroid.android.starter.kit.widget.LoadingLayout;
import java.util.ArrayList;

public abstract class RecyclerViewFragment<E extends Entitiy> extends StarterFragment
    implements LoadingLayout.OnButtonClickListener,
    EasyViewHolder.OnItemClickListener,
    EasyViewHolder.OnItemLongClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    PaginatorCallback<E> {

  LoadingLayout mLoadingLayout;
  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  Paginator<E> mPagePaginator;
  ViewHandler mViewHandler;

  public abstract void bindViewHolders(EasyRecyclerAdapter adapter);

  public abstract Paginator<E> buildPaginator();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPagePaginator = buildPaginator();
    mViewHandler = buildViewHandler();
    mViewHandler.onCreate();
    final EasyRecyclerAdapter adapter = mViewHandler.getAdapter();
    adapter.setOnClickListener(this);
    adapter.setOnLongClickListener(this);
    bindViewHolders(adapter);
  }

  public Paginator<E> getPagePaginator() {
    return mPagePaginator;
  }

  public ViewHandler buildViewHandler() {
    return new RecyclerViewHandler(getContext());
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
    setupRecyclerView();
  }

  @Override public void onResume() {
    super.onResume();
    updateView();

    if (!mPagePaginator.dataHasLoaded()) {
      mPagePaginator.refresh();
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (mPagePaginator.isLoading()) {
      mPagePaginator.cancel();
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mViewHandler.onDestroyView(mRecyclerView);
    mLoadingLayout = null;
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mViewHandler.onDestroy();
    mPagePaginator = null;
    mViewHandler = null;
  }

  /**
   * setup
   */
  private void setupRecyclerView() {
    mViewHandler.setupAdapter(mRecyclerView);
  }

  public void buildRecyclerView() {
    // set layout manager
    mRecyclerView.setLayoutManager(buildLayoutManager());
    // add item decoration
    mRecyclerView.addItemDecoration(buildItemDecoration());
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

  private void updateView() {
    if (mLoadingLayout == null) {
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
    if (isEmpty()) {
      mLoadingLayout.showLoadingView();
    }
  }

  @Override public void endRequest() {
    if (mPagePaginator.isRefresh()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    updateView();
  }

  @Override public void respondSuccess(ArrayList<E> data) {
    mViewHandler.notifyDataSetChanged(mPagePaginator.items());
  }

  @Override public void respondWithError(Throwable error) {
  }

  @Override public void errorNotFound(ErrorModel errorModel) {
  }

  @Override public void errorUnprocessable(ErrorModel errorModel) {

  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {

  }

  @Override public void errorForbidden(ErrorModel errorModel) {

  }

  @Override public void eNetUnreach(Throwable t) {

  }

  @Override public void errorSocketTimeout(Throwable t) {

  }

  @Override public void error(ErrorModel errorModel) {

  }
}
