/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true) public class Feed extends Entity {

  public int id;
  public String content;
  @JsonProperty("created_at") public int createdAt;

  public User user;
  public ArrayList<Image> images;

  public Feed() {
  }

  public Feed(Parcel source) {
    this.id = source.readInt();
    this.content = source.readString();
    this.createdAt = source.readInt();

    this.user = source.readParcelable(getClass().getClassLoader());
    this.images = new ArrayList<>();
    source.readTypedList(images, Image.CREATOR);
  }

  public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
    public Feed createFromParcel(Parcel source) {
      return new Feed(source);
    }

    public Feed[] newArray(int size) {
      return new Feed[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(content);
    dest.writeInt(createdAt);
    dest.writeParcelable(user, flags);
    dest.writeTypedList(images);
  }
}
