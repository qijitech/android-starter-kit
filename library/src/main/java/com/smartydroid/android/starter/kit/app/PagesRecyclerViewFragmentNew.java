/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import com.smartydroid.android.starter.kit.contracts.Pagination.PaginationEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.Pagination;

public abstract class PagesRecyclerViewFragmentNew<E extends Entitiy>
    extends RecyclerViewNewFragment<E> implements PaginationEmitter<E> {

  @Override public Paginator<E> buildPaginator() {
    return new Pagination.Builder<E>()
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
