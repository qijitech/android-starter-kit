/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Result<T> implements Serializable {

  /**
   * 返回的错误码、0代表成功
   */
  @JsonProperty("code") public int mCode;

  /**
   * 提示信息
   */
  @JsonProperty("message") public String mMsg;

  /**
   * 当前页面
   */
  @JsonProperty("page") public long mPage;

  /**
   * 总页码
   */
  @JsonProperty("page_size") public long mPageSize;

  /**
   * 总数据条数
   */
  @JsonProperty("total_size") public long mTotalSize;

  /**
   * 返回的数据 T
   */
  @JsonProperty("data") public T mData;

  /**
   * 请求是否成功、0代表成功
   */
  public boolean isSuccessed() {
    return mCode == 0;
  }

}
