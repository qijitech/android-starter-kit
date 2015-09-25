/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet implements Parcelable {

  public Integer id;
  public String source;
  public String content;

  @JsonProperty("published_at") public Integer publishedAt;

  public Tweet() {
  }

  public Tweet(Parcel source) {
    this.id = source.readInt();
    this.source = source.readString();
    this.content = source.readString();
    this.publishedAt = source.readInt();
  }

  public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
    public Tweet createFromParcel(Parcel source) {
      return new Tweet(source);
    }
    public Tweet[] newArray(int size) {
      return new Tweet[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(source);
    dest.writeString(content);
    dest.writeInt(publishedAt);
  }
}

