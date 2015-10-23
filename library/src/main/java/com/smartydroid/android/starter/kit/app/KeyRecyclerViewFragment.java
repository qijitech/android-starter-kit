/**
 * Created by YuGang Yang on September 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import com.smartydroid.android.starter.kit.contracts.Pagination.IdEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.KeyPaginator;

public abstract class KeyRecyclerViewFragment<E extends Entitiy> extends RecyclerViewFragment<E>
    implements IdEmitter<E> {

  @Override public Paginator<E> buildPaginator() {
    return new KeyPaginator.Builder<E>()
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
