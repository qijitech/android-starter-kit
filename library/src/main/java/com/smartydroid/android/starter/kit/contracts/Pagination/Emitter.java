/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

public interface Emitter<T> {

  void beforeRefresh();

  void beforeLoadMore();

  T register(final T item);

  Object getKeyForData(T item);
}
