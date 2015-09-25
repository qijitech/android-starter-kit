/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;

public class TweetViewHolder extends EasyViewHolder<Tweet> {

  @Bind(R.id.text_tweet_content) TextView mTweetContent;
  @Bind(R.id.text_tweet_published_time) TextView mTweetPublishAt;
  @Bind(R.id.text_tweet_source) TextView mTweetSource;

  public TweetViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_tweet);
    ButterKnife.bind(this, itemView);
  }

  @Override public void bindTo(Tweet tweet) {
    mTweetContent.setText(tweet.content);
    mTweetSource.setText(tweet.source);
    mTweetPublishAt.setText(String.valueOf(tweet.publishedAt));
  }
}
