/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;
import com.paginate.Paginate;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.contracts.Pagination.PaginatorContract;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import java.util.ArrayList;
import support.ui.content.RequiresContent;

@RequiresContent public abstract class RecyclerViewFragment<E extends Entity>
    extends CallbackFragment<ArrayList<E>>
    implements Paginate.Callbacks, EasyViewHolder.OnItemClickListener,
    EasyViewHolder.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener,
    PaginatorCallback<E> {

  ViewGroup mContainer;
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
    mContainer = ButterKnife.findById(view, R.id.support_ui_content_container);
    mSwipeRefreshLayout = ButterKnife.findById(view, R.id.swipe_refresh_layout);
    mRecyclerView = ButterKnife.findById(view, R.id.support_ui_content_recycler_view);
    mSwipeRefreshLayout.setOnRefreshListener(this);

    contentPresenter.attachContainer(mContainer);
    contentPresenter.attachContentView(mSwipeRefreshLayout);

    buildRecyclerView();
  }

  @Override public void onResume() {
    super.onResume();
    mPaginate.setHasMoreDataToLoad(false);
    if (!mPagePaginator.dataHasLoaded()) {
      refresh();
    }
    updateView();
  }

  @Override public void onPause() {
    super.onPause();
    if (isNotNull(mPagePaginator)) {
      mPagePaginator.cancel();
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
    mPaginate = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPagePaginator = null;
    mAdapter = null;
  }

  public void buildRecyclerView() {
    // set layout manager
    mRecyclerView.setLayoutManager(buildLayoutManager());

    // add item decoration
    //mRecyclerView.addItemDecoration(RecyclerViewUtils.buildItemDecoration());
    //mRecyclerView.setItemAnimator(new SlideInUpAnimator());

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

  public SwipeRefreshLayout getSwipeRefreshLayout() {
    return mSwipeRefreshLayout;
  }

  public RecyclerView getRecyclerView() {
    return mRecyclerView;
  }

  public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  private void updateView() {
    if (!isNotNull(contentPresenter) || isDetached()) {
      return;
    }
    if (!isEmpty()) {
      contentPresenter.displayContentView();
      return;
    }
    if (isNotNull(mPagePaginator) && !mPagePaginator.dataHasLoaded()) {
      contentPresenter.displayLoadView();
      return;
    }
    if (mPagePaginator.hasError()) {
      contentPresenter.displayErrorView();
    } else {
      contentPresenter.displayEmptyView();
    }
  }

  public boolean isEmpty() {
    return isNotNull(mPagePaginator) && mPagePaginator.isEmpty();
  }

  @Override public void onRefresh() {
    if (isNotNull(mPagePaginator) && !mPagePaginator.isLoading()) {
      mPagePaginator.refresh();
    } else if (isNotNull(mSwipeRefreshLayout)) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void onEmptyViewClick(View view) {
    onRefresh();
  }

  @Override public void onErrorViewClick(View view) {
    onRefresh();
  }

  public E getItem(int position) {
    if (isNotNull(mPagePaginator) && !mPagePaginator.isEmpty()) {
      return mPagePaginator.items().get(position);
    }
    return null;
  }

  @Override public void onItemClick(int position, View view) {
  }

  @Override public boolean onLongItemClicked(int position, View view) {
    return false;
  }

  ///////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////

  @Override public void errorNotFound(ErrorModel errorModel) {
    super.errorNotFound(errorModel);
    if (getPagePaginator() != null && getPagePaginator().isRefresh()) {
      getPagePaginator().clearAll();
    }
  }

  @Override public void startRequest() {
    if (isEmpty() && isNotNull(contentPresenter)) {
      contentPresenter.displayLoadView();
    }
  }

  @Override public void endRequest() {
    if (isNotNull(mSwipeRefreshLayout) && isNotNull(mPagePaginator) && mPagePaginator.isRefresh()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    updateView();
  }

  @Override public void respondSuccess(ArrayList<E> data) {
    if (isNotNull(mAdapter) && isNotNull(mPagePaginator)) {
      mAdapter.addAll(mPagePaginator.items());
    }
  }

  public void refresh() {
    if (isNotNull(mPagePaginator) &&
        !mPagePaginator.isLoading()) {
      mPagePaginator.refresh();
    }
  }

  @Override public void onLoadMore() {
  }

  @Override public boolean isLoading() {
    return isNotNull(mPagePaginator) && mPagePaginator.isLoading();
  }

  @Override public boolean hasLoadedAllItems() {
    return isNotNull(mPagePaginator) && !mPagePaginator.canLoadMore() && !mPagePaginator.hasError();
  }

  public boolean isNotNull(Object object) {
    return object != null;
  }
}
