package com.smartydroid.android.kit.demo.model.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartydroid.android.starter.kit.model.entity.Entity;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Image extends Entity {

  public Integer id;
  public String url;
  public int width;
  public int height;

  @SuppressWarnings("unused") public Image() {
    /*Required empty bean constructor*/
  }

  public Image(Parcel source) {
    this.id = source.readInt();
    this.url = source.readString();
    this.width = source.readInt();
    this.height = source.readInt();
  }

  public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
    public Image createFromParcel(Parcel source) {
      return new Image(source);
    }

    public Image[] newArray(int size) {
      return new Image[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(url);
    dest.writeInt(width);
    dest.writeInt(height);
  }

  public Uri uri() {
    if (TextUtils.isEmpty(url)) return null;

    if (url.startsWith("http://")) {
      return Uri.parse(url);
    }

    return null;
  }

  public Uri uri(int w, int h) {
    if (TextUtils.isEmpty(url)) return null;

    if (url.startsWith("http://")) {
      return Uri.parse(url);
    }

    // 200w_300h_1e_1c
    //String.format("%s%dw_%dh_1e_1c", Profile.OSS_ENDPOINT + url, w, h);
    return null;
  }
}
