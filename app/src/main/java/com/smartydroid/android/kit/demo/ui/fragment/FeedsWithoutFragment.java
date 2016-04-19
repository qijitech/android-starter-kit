package com.smartydroid.android.kit.demo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import com.smartydroid.android.kit.demo.api.ApiService;
import com.smartydroid.android.kit.demo.api.service.FeedService;
import com.smartydroid.android.kit.demo.model.entity.Feed;
import com.smartydroid.android.kit.demo.ui.view.AppEmptyView;
import com.smartydroid.android.kit.demo.ui.viewholder.FeedViewHolderFactory;
import com.smartydroid.android.kit.demo.ui.viewholder.FeedsTextViewHolder;
import com.smartydroid.android.starter.kit.app.StarterRecyclerFragment;
import java.util.ArrayList;
import retrofit2.Call;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.content.RequiresContent;

/**
 * Created by YuGang Yang on February 13, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
@RequiresContent(emptyView = AppEmptyView.class)
public class FeedsWithoutFragment extends StarterRecyclerFragment<Feed> {
  private FeedService mFeedService;

  public static Fragment create() {
    return new FeedsWithoutFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFeedService = ApiService.createFeedService();
  }

  @Override public void viewHolderFactory(EasyRecyclerAdapter adapter) {
    adapter.viewHolderFactory(new FeedViewHolderFactory(getContext()));
  }

  @Override public Call<ArrayList<Feed>> paginate(int page, int perPage) {
    return mFeedService.getFeedList(1, 2);
  }

  @Override public Object getKeyForData(Feed item) {
    return item.id;
  }

  @Override public void bindViewHolders(EasyRecyclerAdapter adapter) {
    adapter.bind(Feed.class, FeedsTextViewHolder.class);
  }

  @Override public void onItemClick(int position, View view) {
    super.onItemClick(position, view);
    final Feed feed = getItem(position);
    Toast.makeText(getContext(), feed.content, Toast.LENGTH_SHORT).show();
  }

}
