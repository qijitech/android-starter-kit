/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.os.Bundle;
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
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit.Call;

public class ListActivity extends AppCompatActivity
    implements PageCallback<List<Tweet>>, Paginator.Emitter<Tweet> {

  private LoadingLayout mLoadingLayout;
  private View mContentView;
  private RecyclerView mRecyclerView;
  private EasyRecyclerAdapter mRecyclerAdapter;

  private FeedService mFeedService;
  private Call<Result<List<Tweet>>> mCallFuture;
  private PagePaginator<Tweet> mPagePaginator;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    mLoadingLayout = (LoadingLayout) findViewById(R.id.container_loading_layout);
    mContentView = mLoadingLayout.getContentView();
    mRecyclerView = ViewUtils.getView(mContentView, android.R.id.list);
    setupRecyclerView();
    mPagePaginator = new PagePaginator.Builder<Tweet>()
        .setEmitter(this).setPageCallback(this).build();
    mFeedService = StarterNetwork.createFeedService();
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    updateView();
    mPagePaginator.refresh();
  }

  @Override protected void onResume() {
    super.onResume();
    if (mCallFuture != null) {
      mCallFuture.enqueue(mPagePaginator);
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (mCallFuture != null) {
      mCallFuture.cancel();
    }
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
    ensureRecyclerAdapter();
    mRecyclerView.setAdapter(mRecyclerAdapter);
  }

  private void ensureRecyclerAdapter() {
    if (mRecyclerAdapter == null) {
      mRecyclerAdapter = new TweetAdapter(this);
    }
  }

  private boolean isEmpty() {
    return mPagePaginator.isEmpty();
  }

  @Override public void paginate(int page, int perPage) {
    mCallFuture = mFeedService.getTweetList(page, perPage);
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
    updateView();
  }
}
