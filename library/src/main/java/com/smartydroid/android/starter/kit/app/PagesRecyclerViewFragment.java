/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import com.smartydroid.android.starter.kit.contracts.Pagination.PagesEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.PagesPaginator;

public abstract class PagesRecyclerViewFragment<E extends Entitiy> extends RecyclerViewFragment<E>
    implements PagesEmitter<E> {

  @Override public Paginator<E> buildPaginator() {
    return new PagesPaginator.Builder<E>()
        .emitter(this)
        .callback(this)
        .build();
  }

  @Override public E register(E item) {
    return item;
  }

  @Override public void beforeRefresh() {
  }

  @Override public void beforeLoadMore() {
  }
}
