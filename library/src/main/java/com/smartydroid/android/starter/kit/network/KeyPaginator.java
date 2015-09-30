/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.IdEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.IdPaginator;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.callback.PaginationCallback;
import java.util.List;
import retrofit.Call;

public class KeyPaginator<T extends Entitiy> extends PaginatorImpl<T> implements IdPaginator<T> {

  private T nextItem;
  private T previousItem;

  public static class Builder<T extends Entitiy> {
    private IdEmitter<T> emitter;
    private PaginationCallback<T> callback;

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

    public Builder<T> emitter(IdEmitter<T> emitter) {
      this.emitter = emitter;
      return this;
    }

    public Builder<T> callback(PaginationCallback<T> callback) {
      this.callback = callback;
      return this;
    }

    public Builder<T> perPage(int perPage) {
      this.perPage = perPage;
      return this;
    }
  }

  private KeyPaginator(IdEmitter<T> emitter, PaginationCallback<T> callback, int perPage) {
    super(emitter, callback, perPage);
  }

  @Override protected Call<DataArray<T>> paginate(boolean isRefresh) {
    final IdEmitter<T> idEmitter = (IdEmitter<T>) mEmitter;
    return (Call<DataArray<T>>) idEmitter.paginate(previousItem, nextItem, perPage());
  }

  @Override protected void processPage(DataArray<T> dataArray) {
    mHasMore = dataArray.size() >= mPerPage;

    if (!dataArray.isNull()) {
      final List<T> items = dataArray.data();
      nextItem = items.get(items.size() - 1);
      previousItem = items.get(0);
    }
  }

  @Override public T previousPageItem() {
    return null;
  }

  @Override public T nextPageItem() {
    return null;
  }
}
