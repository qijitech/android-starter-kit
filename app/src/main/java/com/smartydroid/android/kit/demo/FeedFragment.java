/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.os.Bundle;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.smartydroid.android.starter.kit.app.RecyclerViewFragment;
import com.smartydroid.android.starter.kit.network.Result;
import java.util.List;
import retrofit.Call;

public class FeedFragment extends RecyclerViewFragment<Tweet> {

  private FeedService mFeedService;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFeedService = StarterNetwork.createFeedService();
  }

  @Override public void bindViewHolders(EasyRecyclerAdapter adapter) {
    adapter.bind(Tweet.class, TweetViewHolder.class);
  }

  @Override public Call<Result<List<Tweet>>> paginate(int page, int pageSize) {
    return mFeedService.getTweetList(page, pageSize);
  }

  @Override public Object getKeyForData(Tweet item) {
    return item.id;
  }
}
