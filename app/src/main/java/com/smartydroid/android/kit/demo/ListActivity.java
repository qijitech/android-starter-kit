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
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public class ListActivity extends AppCompatActivity {

  private boolean mFirstLoad;
  private boolean mHasError;

  private LoadingLayout mLoadingLayout;
  private View mContentView;
  private RecyclerView mRecyclerView;
  private EasyRecyclerAdapter mRecyclerAdapter;
  private List<Tweet> mItems = new ArrayList<>();

  private FeedService mFeedService;
  private Call<Result<List<Tweet>>> mCallFuture;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    mLoadingLayout = (LoadingLayout) findViewById(R.id.container_loading_layout);
    mContentView = mLoadingLayout.getContentView();
    mRecyclerView = ViewUtils.getView(mContentView, android.R.id.list);
    setupRecyclerView();

    mFeedService = StarterNetwork.createFeedService();
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    setup();
    updateView();
  }

  @Override protected void onResume() {
    super.onResume();
    loadData();
  }

  private void loadData() {
    mCallFuture = mFeedService.getTweetList("1", "20");
    mCallFuture.enqueue(new Callback<Result<List<Tweet>>>() {
      @Override public void onResponse(Response<Result<List<Tweet>>> response) {
        if (response.isSuccess()) {
          Result<List<Tweet>> result = response.body();
          if (result.isSuccessed()) {
            if (result.mData != null && !result.mData.isEmpty()) {
              // 有数据
              mItems.addAll(result.mData);
              showContentView();
            }
          }
        }
      }

      @Override public void onFailure(Throwable t) {
        mLoadingLayout.showEmptyView();
      }
    });
  }

  private void setup() {
    mFirstLoad = true;
    mHasError = false;
  }

  private void updateView() {
    if (! isEmpty()) {
      showContentView();
    } else if (mFirstLoad){
      mLoadingLayout.showLoadingView();
    } else if (mHasError) {
      mLoadingLayout.showErrorView();
    } else {
      mLoadingLayout.showEmptyView();
    }
  }

  private void showContentView() {
    mLoadingLayout.showContentView();
    mRecyclerAdapter.addAll(mItems);
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
    return mItems == null || mItems.isEmpty();
  }
}
