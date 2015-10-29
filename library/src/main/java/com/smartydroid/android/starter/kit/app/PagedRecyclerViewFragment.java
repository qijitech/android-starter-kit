/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import com.smartydroid.android.starter.kit.contracts.Pagination.PageEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.PagePaginator;

public abstract class PagedRecyclerViewFragment<E extends Entitiy>
    extends RecyclerViewFragment<E> implements PageEmitter<E> {

  @Override public Paginator<E> buildPaginator() {
    return new PagePaginator.Builder<E>()
        .emitter(this)
        .callback(this).build();
  }

  @Override public E register(E item) {
    return item;
  }

  @Override public void beforeRefresh() {
  }

  @Override public void beforeLoadMore() {
  }
}
