/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import com.smartydroid.android.starter.kit.contracts.Pagination.PageEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PaginatorContract;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.PagePaginator;

public abstract class StarterPagedFragment<E extends Entity>
    extends RecyclerViewPagedFragment<E> implements PageEmitter<E> {

  @Override public PaginatorContract<E> buildPaginator() {
    return new PagePaginator.Builder<E>()
        .emitter(this)
        .perPage(perPage())
        .callback(this).build();
  }

  public int perPage() {
    return 0;
  }

  @Override public E register(E item) {
    return item;
  }

  @Override public void beforeRefresh() {
  }

  @Override public void beforeLoadMore() {
  }
}
