/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.contracts.Pagination;

import com.smartydroid.android.starter.kit.model.entity.Entity;

public interface PagePaginator<T extends Entity> extends Paginator<T> {

  /**
   * Determine the current page being paginated.
   *
   * @return curent page
   */
  int currentPage();

}
