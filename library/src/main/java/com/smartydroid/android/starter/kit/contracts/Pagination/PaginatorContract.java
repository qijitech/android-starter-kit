/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.entity.Entity;
import java.util.List;

public interface PaginatorContract<T extends Entity> {

  /**
   * Get all of the items being paginated.
   *
   * @return collection
   */
  List<T> items();

  /**
   * Determine how many items are being shown per page.
   */
  int perPage();

  /**
   * Determine if there is more items in the data store.
   *
   * @return has more
   */
  boolean hasMorePages();

  /**
   * Determine if the list of items is empty or not.
   */
  boolean isEmpty();

  boolean hasError();

  boolean dataHasLoaded();

  boolean canLoadMore();

  boolean isRefresh();

  boolean isLoading();

  void refresh();

  void loadMore();

  void cancel();

  void clearAll();

  enum LoadStyle {
    REFRESH,
    LOAD_MORE,
  }
}
