/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;

@JsonIgnoreProperties(ignoreUnknown = true) public class News extends Entitiy {

  public int id;
  public String title;
  public String subtitle;
  public String thumbnail;
  public int reviewedAt;

  public News() {
  }

  public News(Parcel source) {
    this.id = source.readInt();
    this.title = source.readString();
    this.subtitle = source.readString();
    this.thumbnail = source.readString();
    this.reviewedAt = source.readInt();
  }

  public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
    public News createFromParcel(Parcel source) {
      return new News(source);
    }

    public News[] newArray(int size) {
      return new News[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(title);
    dest.writeString(subtitle);
    dest.writeString(thumbnail);
    dest.writeInt(reviewedAt);
  }
}
