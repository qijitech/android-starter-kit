/**
 * Created by YuGang Yang on November 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewHandler implements ViewHandler {

  private final Context context;
  private EasyRecyclerAdapter mAdapter;

  public RecyclerViewHandler(Context context) {
    this.context = context;
  }

  @Override public void onCreate() {
    mAdapter = new EasyRecyclerAdapter(context);
  }

  @Override public void onDestroyView(RecyclerView recyclerView) {
  }

  @Override public void onDestroy() {
    mAdapter = null;
  }

  @Override public void setupAdapter(RecyclerView recyclerView) {
    recyclerView.setAdapter(mAdapter);
  }

  @Override public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  @Override public ILoadMoreView getLoadMoreView() {
    return null;
  }

  @Override public void notifyDataSetChanged(List<?> items) {
    mAdapter.addAll(items);
  }

  @Override
  public void setOnScrollBottomListener(RecyclerView recyclerView, OnScrollBottomListener l) {
  }

  @Override public void setOnLoadMoreClickListener(ILoadMoreView.OnLoadMoreClickListener l) {

  }
}
