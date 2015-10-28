/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.content.Context;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;

public class NewsViewHolder extends EasyViewHolder<News> {

  public NewsViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_news);
    ButterKnife.bind(this, itemView);
  }

  @Override public void bindTo(News tweet) {
  }
}
