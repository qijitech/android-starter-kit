/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.KeyEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.KeyPaginatorContract;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class KeyPaginator<T extends Entity> extends Paginator<T> implements
    KeyPaginatorContract<T> {

  /**
   * Builder key paginator
   */
  public static class Builder<T extends Entity> {
    private KeyEmitter<T> emitter;
    private PaginatorCallback<T> callback;

    private int perPage;

    /** Create the {@link KeyPaginator} instances. */
    public KeyPaginator<T> build() {
      if (callback == null) {
        throw new IllegalArgumentException("PaginationCallback may not be null.");
      }
      if (emitter == null) {
        throw new IllegalArgumentException("Emitter may not be null.");
      }
      ensureSaneDefaults();
      return new KeyPaginator<>(emitter, callback, perPage);
    }

    private void ensureSaneDefaults() {
      if (perPage <= 0) {
        perPage = DEFAULT_PER_PAGE;
      }
    }

    public Builder<T> emitter(KeyEmitter<T> emitter) {
      this.emitter = emitter;
      return this;
    }

    public Builder<T> callback(PaginatorCallback<T> callback) {
      this.callback = callback;
      return this;
    }

    public Builder<T> perPage(int perPage) {
      this.perPage = perPage;
      return this;
    }
  }

  private KeyPaginator(KeyEmitter<T> emitter, PaginatorCallback<T> callback, int perPage) {
    super(emitter, callback, perPage);
  }

  @Override protected Call<ArrayList<T>> paginate() {
    final KeyEmitter<T> keyEmitter = (KeyEmitter<T>) mEmitter;
    if (keyEmitter == null) return null;

    if (isRefresh()) {
     return keyEmitter.paginate(previousPageItem(), null, perPage());
    }

    return keyEmitter.paginate(null, nextPageItem(), perPage());
  }

  @Override protected void processPage(ArrayList<T> dataArray) {
    mHasMore = dataArray.size() >= mPerPage;
  }

  @Override public T previousPageItem() {
    final List<T> items = items();
    return items.isEmpty() ? null : items.get(0);
  }

  @Override public T nextPageItem() {
    final List<T> items = items();
    return items.isEmpty() ? null : items.get(items.size() - 1);
  }
}
