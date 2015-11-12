/**
 * Created by YuGang Yang on November 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.starter.kit.widget.ILoadMoreView;
import java.util.List;

public interface ViewHandler {

  void onCreate();

  void onDestroyView(RecyclerView recyclerView);
  void onDestroy();

  void setupAdapter(RecyclerView recyclerView);

  EasyRecyclerAdapter getAdapter();

  void notifyDataSetChanged(List<?> items);

  void setOnScrollBottomListener(RecyclerView recyclerView, OnScrollBottomListener l);
  void setOnLoadMoreClickListener(ILoadMoreView.OnLoadMoreClickListener l);

  ILoadMoreView getLoadMoreView();

  interface OnScrollBottomListener {
    void OnScrollBottom();
  }
}
