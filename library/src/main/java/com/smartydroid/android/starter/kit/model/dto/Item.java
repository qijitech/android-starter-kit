/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;

public class Item<T extends Entitiy> implements Data<T> {

  /**
   * 返回的错误码、0代表成功
   */
  @JsonProperty("code") public int mCode;

  /**
   * 提示信息
   */
  @JsonProperty("message") public String mMsg;

  /**
   * 返回的数据 T
   */
  @JsonProperty("data") public T mData;

  @Override public int code() {
    return mCode;
  }

  @Override public String msg() {
    return mMsg;
  }

  @Override public T data() {
    return mData;
  }

  @Override public boolean isSuccess() {
    return mCode == 0;
  }

  @Override public boolean isNull() {
    return mData == null;
  }
}
