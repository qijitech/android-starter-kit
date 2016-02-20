package com.smartydroid.android.kit.demo.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartydroid.android.starter.kit.account.Account;
import com.smartydroid.android.starter.kit.model.entity.Entity;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class User extends Entity implements Account {

  public Integer id;
  public String phone;
  public String token;

  public User() {

  }

  public User(Parcel source) {
    this.id = source.readInt();
    this.phone = source.readString();
    this.token = source.readString();
  }

  public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
    public User createFromParcel(Parcel source) {
      return new User(source);
    }

    public User[] newArray(int size) {
      return new User[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(phone);
    dest.writeString(token);
  }

  @Override public String token() {
    return token;
  }

  @Override public Object key() {
    return id;
  }

  @Override public String toJson() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
