/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import com.smartydroid.android.starter.kit.contracts.Pagination.KeyEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PaginatorContract;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.KeyPaginator;

public abstract class StarterKeysFragment<E extends Entity>
    extends RecyclerViewPagedFragment<E> implements KeyEmitter<E> {

  @Override public PaginatorContract<E> buildPaginator() {
    return new KeyPaginator.Builder<E>()
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
