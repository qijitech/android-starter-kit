package com.smartydroid.android.kit.demo.ui.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import com.carlosdelachica.easyrecycleradapters.adapter.BaseEasyViewHolderFactory;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.smartydroid.android.kit.demo.model.entity.Feed;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class FeedViewHolderFactory extends BaseEasyViewHolderFactory {

  private static final int VIEW_TYPE_TEXT = 0;
  private static final int VIEW_TYPE_IMAGE_SINGLE = 1;
  private static final int VIEW_TYPE_IMAGE_MULTI = 2;

  public FeedViewHolderFactory(Context context) {
    super(context);
  }

  @Override public EasyViewHolder create(int viewType, ViewGroup parent) {
    switch (viewType) {
      case VIEW_TYPE_TEXT:
        return new FeedsTextViewHolder(parent.getContext(), parent);
      case VIEW_TYPE_IMAGE_SINGLE:
        return new FeedsSingleImageViewHolder(parent.getContext(), parent);
      case VIEW_TYPE_IMAGE_MULTI:
        return new FeedsMultiImageViewHolder(parent.getContext(), parent);
    }
    return null;
  }

  @Override public int itemViewType(Object object) {
    if (object instanceof Feed) {
      Feed feed = (Feed) object;
      if (feed.images == null || feed.images.size() <= 0) {
        return VIEW_TYPE_TEXT;
      }
      if (feed.images.size() == 1) {
        return VIEW_TYPE_IMAGE_SINGLE;
      }
      return VIEW_TYPE_IMAGE_MULTI;
    }
    return -1;
  }
}
