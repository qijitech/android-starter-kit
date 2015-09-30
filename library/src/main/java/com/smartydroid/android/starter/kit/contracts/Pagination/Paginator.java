/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import java.util.List;
import retrofit.Callback;

public interface Paginator<T extends Entitiy> extends Callback<DataArray<T>> {

  /**
   * Get all of the items being paginated.
   *
   * @return collection
   */
  List<T> items();

  /**
   * Get the "index" of the first item being paginated.
   */
  T fisrtItem();

  /**
   * Get the "index" of the last item being paginated.
   */
  T lastItem();

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
}
