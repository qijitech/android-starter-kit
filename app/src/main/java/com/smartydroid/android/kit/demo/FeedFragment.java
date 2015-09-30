/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.os.Bundle;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.starter.kit.app.PagesRecyclerViewFragment;
import com.smartydroid.android.starter.kit.model.dto.PagesData;
import retrofit.Call;

public class FeedFragment extends PagesRecyclerViewFragment<Tweet> {

  private FeedService mFeedService;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFeedService = StarterNetwork.createFeedService();
  }

  @Override public void bindViewHolders(EasyRecyclerAdapter adapter) {
    adapter.bind(Tweet.class, TweetViewHolder.class);
  }

  @Override public Call<PagesData<Tweet>> paginate(int page, int perPage) {
    return mFeedService.getTweetList(page, perPage);
  }

  @Override public Object getKeyForData(Tweet item) {
    return item.id;
  }
}
