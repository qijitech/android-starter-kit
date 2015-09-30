/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.model.dto;

import java.io.Serializable;

public interface Data<T> extends Serializable {

  /**
   * 返回的错误码、0代表成功
   */
  int code();

  /**
   * 提示信息
   */
  String msg();

  /**
   * 返回的数据 T
   */
  T data();

  /**
   * 请求是否成功
   */
  boolean isSuccess();

  boolean isNull();
}
