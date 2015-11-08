/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.ui.fragment;

import android.os.Bundle;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.kit.demo.api.ApiService;
import com.smartydroid.android.kit.demo.api.service.NewsService;
import com.smartydroid.android.kit.demo.model.entity.News;
import com.smartydroid.android.kit.demo.ui.viewholder.NewsViewHolder;
import com.smartydroid.android.starter.kit.app.StarterPagedFragment;
import java.util.ArrayList;
import retrofit.Call;

public class NewsFragment extends StarterPagedFragment<News> {

  private NewsService mNewsService;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mNewsService = ApiService.createNewsService();
  }

  @Override public Call<ArrayList<News>> paginate(int page, int perPage) {
    return mNewsService.getNewsList(page, perPage, 2);
  }

  @Override public Object getKeyForData(News item) {
    return item.id;
  }

  @Override public void bindViewHolders(EasyRecyclerAdapter adapter) {
    adapter.bind(News.class, NewsViewHolder.class);
  }
}
