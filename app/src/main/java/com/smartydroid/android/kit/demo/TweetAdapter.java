/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.content.Context;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;

public class TweetAdapter extends EasyRecyclerAdapter {
  public TweetAdapter(Context context) {
    super(context);
    bind(Tweet.class, TweetViewHolder.class);
  }
}
