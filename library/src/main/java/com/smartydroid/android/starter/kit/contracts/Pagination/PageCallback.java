/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.network.Result;

public interface PageCallback<T> {

  void onRequestComplete(Result<T> result);

  void onRequestComplete(int code, String error);

  void onRequestFailure(Result<T> result);

  void onRequestFailure(Throwable error);

  void onFinish();
}
