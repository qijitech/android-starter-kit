/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;

public interface PageCallback<T extends Entitiy> {

  void onRequestComplete(DataArray<T> dataArray);

  void onRequestComplete(int code, String error);

  void onRequestFailure(DataArray<T> dataArray);

  void onRequestFailure(Throwable error);

  void onFinish();
}
