package com.smartydroid.android.kit.demo.ui.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import com.carlosdelachica.easyrecycleradapters.adapter.BaseEasyViewHolderFactory;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.smartydroid.android.kit.demo.model.entity.Feed;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public class FeedViewHolderFactory extends BaseEasyViewHolderFactory {

  private static final int VIEW_TYPE_TEXT = 0;
  private static final int VIEW_TYPE_IMAGE = 1;

  public FeedViewHolderFactory(Context context) {
    super(context);
  }

  @Override public EasyViewHolder create(int viewType, ViewGroup parent) {
    switch (viewType) {
      case VIEW_TYPE_TEXT:
        return new FeedsViewHolder(parent.getContext(), parent);
      case VIEW_TYPE_IMAGE:
        return new FeedsImageViewHolder(parent.getContext(), parent);
    }
    return null;
  }

  @Override public int itemViewType(Object object) {
    if (object instanceof Feed) {
      Feed feed = (Feed) object;
      if (feed.images == null || feed.images.size() <= 0) {
        return VIEW_TYPE_TEXT;
      }
      return VIEW_TYPE_IMAGE;
    }
    return -1;
  }
}
