/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.ui.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.kit.demo.model.entity.News;

public class NewsViewHolder extends EasyViewHolder<News> {

  @Bind(R.id.text_tweet_content) TextView mTweetContent;
  @Bind(R.id.text_tweet_published_time) TextView mTweetPublishAt;
  @Bind(R.id.text_tweet_source) TextView mTweetSource;

  public NewsViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_news);
    ButterKnife.bind(this, itemView);
  }

  @Override public void bindTo(News news) {

    mTweetContent.setText(news.title);
    mTweetSource.setText(news.subtitle);
    mTweetPublishAt.setText(String.valueOf(news.reviewedAt));
  }
}
