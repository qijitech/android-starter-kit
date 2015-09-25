/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.starter.kit.LoadingLayout;
import com.smartydroid.android.starter.kit.contracts.Pagination.PageCallback;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.network.PagePaginator;
import com.smartydroid.android.starter.kit.network.Result;
import com.smartydroid.android.starter.kit.utilities.RecyclerViewHandler;
import com.smartydroid.android.starter.kit.utilities.ViewHandler;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.smartydroid.android.starter.kit.widget.LoadMoreView;
import java.util.List;
import retrofit.Call;

public class ListActivity extends AppCompatActivity
    implements PageCallback<List<Tweet>>, Paginator.Emitter<Tweet>, SwipeRefreshLayout.OnRefreshListener {

  private LoadingLayout mLoadingLayout;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private RecyclerView mRecyclerView;
  private EasyRecyclerAdapter mRecyclerAdapter;

  private FeedService mFeedService;
  private PagePaginator<Tweet> mPagePaginator;

  private LoadMoreView mLoadMoreView;
  private RecyclerViewHandler mRecyclerViewHandler;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    mLoadingLayout = (LoadingLayout) findViewById(R.id.container_loading_layout);
    mSwipeRefreshLayout = mLoadingLayout.getContentView();
    mSwipeRefreshLayout.setOnRefreshListener(this);
    mRecyclerView = ViewUtils.getView(mSwipeRefreshLayout, android.R.id.list);
    mPagePaginator = new PagePaginator.Builder<Tweet>()
        .setEmitter(this).setPageCallback(this).build();
    mFeedService = StarterNetwork.createFeedService();

    mLoadMoreView = new LoadMoreView();
    mRecyclerViewHandler = new RecyclerViewHandler();

    setupRecyclerView();
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    updateView();
  }

  @Override protected void onResume() {
    super.onResume();
    if (! mPagePaginator.dataHasLoaded()) {
      mPagePaginator.refresh();
    }
  }

  @Override protected void onPause() {
    super.onPause();
    mPagePaginator.cancel();
  }

  private void updateView() {
    if (!isEmpty()) {
      showContentView();
    } else if (! mPagePaginator.dataHasLoaded()) {
      mLoadingLayout.showLoadingView();
    } else if (mPagePaginator.hasError()) {
      mLoadingLayout.showErrorView();
    } else {
      mLoadingLayout.showEmptyView();
    }
  }

  private void showContentView() {
    mLoadingLayout.showContentView();
  }

  private void setupRecyclerView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerAdapter = new TweetAdapter(this);
    mRecyclerViewHandler.handleSetAdapter(mRecyclerView, mRecyclerAdapter, mLoadMoreView,
        mLoadMoreListener);
    mRecyclerViewHandler.setOnScrollBottomListener(mRecyclerView, onScrollBottomListener);
  }

  @Override public void onRefresh() {
    if (! mPagePaginator.isLoading()) {
      mPagePaginator.refresh();
    } else {
      mSwipeRefreshLayout.setRefreshing(false);
    }
  }

  private View.OnClickListener mLoadMoreListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      mPagePaginator.loadMore();
    }
  };

  private ViewHandler.OnScrollBottomListener onScrollBottomListener = new ViewHandler.OnScrollBottomListener() {
    @Override public void onScorllBootom() {
      if (mPagePaginator.canLoadMore()) {
        mPagePaginator.loadMore();
      }
    }
  };

  private boolean isEmpty() {
    return mPagePaginator.isEmpty();
  }

  @Override public Call<Result<List<Tweet>>> paginate(int page, int perPage) {
    return mFeedService.getTweetList(page, perPage);
  }

  @Override public void beforeRefresh() {
  }

  @Override public void beforeLoadMore() {
  }

  @Override public Tweet register(Tweet item) {
    return item;
  }

  @Override public Object getKeyForData(Tweet item) {
    return item.id;
  }

  @Override public void onRequestComplete(Result<List<Tweet>> result) {
    mRecyclerAdapter.addAll(mPagePaginator.items());
  }

  @Override public void onRequestComplete(int code, String error) {

  }

  @Override public void onRequestFailure(Result<List<Tweet>> result) {
  }

  @Override public void onRequestFailure(Throwable error) {

  }

  @Override public void onFinish() {
    if (mPagePaginator.isRefresh()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
    updateView();
  }
}
