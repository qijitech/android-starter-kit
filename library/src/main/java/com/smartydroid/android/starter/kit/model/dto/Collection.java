/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import java.util.List;

public class Collection<T extends Entitiy> implements DataArray<T> {
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
  @JsonProperty("page") public int mCurrentPage;

  /**
   * 总页码
   */
  @JsonProperty("page_size") public int mPerPage;

  /**
   * 总数据条数
   */
  @JsonProperty("total_size") public long mTotalSize;

  /**
   * 返回的数据 T
   */
  @JsonProperty("data") public List<T> mData;

  @Override public int code() {
    return mCode;
  }

  @Override public String msg() {
    return mMsg;
  }

  @Override public int currentPage() {
    return mCurrentPage;
  }

  @Override public int perPage() {
    return mPerPage;
  }

  @Override public long total() {
    return mTotalSize;
  }

  @Override public List<T> data() {
    return mData;
  }

  @Override public boolean isSuccess() {
    return code() == 0;
  }

  @Override public boolean isNull() {
    return mData == null || mData.size() <= 0;
  }

  @Override public int size() {
    return mData == null ? 0 : mData.size();
  }
}


