/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PagePaginator;
import com.smartydroid.android.starter.kit.contracts.Pagination.PaginationEmitter;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import java.util.ArrayList;
import retrofit.Call;

public class Pagination<T extends Entitiy> extends Paginator<T> implements PagePaginator<T> {

  private static final int DEFAULT_FIRST_PAGE = 1;
  int mFirstPage = DEFAULT_FIRST_PAGE;

  int mCurrentPage;

  private Pagination(Emitter<T> emitter, PaginatorCallback<T> callback, int startPage,
      int perPage) {
    super(emitter, callback, perPage);
    mFirstPage = startPage;
  }

  @Override public int perPage() {
    return mPerPage;
  }

  @Override public int currentPage() {
    return mCurrentPage;
  }

  @Override protected void processPage(ArrayList<T> dataArray) {
    mHasMore = dataArray.size() >= mPerPage;
  }

  @Override protected Call<ArrayList<T>> paginate(boolean isRefresh) {
    final PaginationEmitter<T> emitter = (PaginationEmitter<T>) mEmitter;
    if (emitter != null) {
      return emitter.paginate(isRefresh ? mFirstPage : (currentPage() + 1), perPage());
    }
    return null;
  }

  public static class Builder<T extends Entitiy> {
    private PaginationEmitter<T> emitter;
    private PaginatorCallback<T> callback;

    private int firstPage;
    private int perPage;

    /** Create the {@link PagesPaginator} instances. */
    public Pagination<T> build() {
      if (callback == null) {
        throw new IllegalArgumentException("PaginationCallback may not be null.");
      }
      if (emitter == null) {
        throw new IllegalArgumentException("Emitter may not be null.");
      }
      ensureSaneDefaults();
      return new Pagination<>(emitter, callback, firstPage, perPage);
    }

    private void ensureSaneDefaults() {
      if (perPage <= 0) {
        perPage = DEFAULT_PER_PAGE;
      }
    }

    public Builder<T> emitter(PaginationEmitter<T> emitter) {
      this.emitter = emitter;
      return this;
    }

    public Builder<T> callback(PaginatorCallback<T> callback) {
      this.callback = callback;
      return this;
    }

    public Builder<T> firstPage(int firstPage) {
      this.firstPage = firstPage;
      return this;
    }

    public Builder<T> perPage(int perPage) {
      this.perPage = perPage;
      return this;
    }
  }
}
