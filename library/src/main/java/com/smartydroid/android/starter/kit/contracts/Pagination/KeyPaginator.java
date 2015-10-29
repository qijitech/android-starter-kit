/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.entity.Entitiy;

public interface KeyPaginator<T extends Entitiy> extends Paginator<T> {

  /**
   * The next item, or null.
   *
   * @return T
   */
  T nextPageItem();

  /**
   * Get the previous item, or null.
   *
   * @return T
   */
  T previousPageItem();
}
