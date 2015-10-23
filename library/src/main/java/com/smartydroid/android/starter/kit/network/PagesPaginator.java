/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PagePaginator;
import com.smartydroid.android.starter.kit.contracts.Pagination.PagesEmitter;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.callback.PaginationCallback;
import retrofit.Call;

public class PagesPaginator<T extends Entitiy> extends PaginatorImpl<T>
    implements PagePaginator<T> {

  private static final int DEFAULT_FIRST_PAGE = 1;
  int mFirstPage = DEFAULT_FIRST_PAGE;

  int mCurrentPage;
  long mTotalSize;

  private PagesPaginator(Emitter<T> emitter, PaginationCallback<T> callback, int startPage,
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

  @Override protected void processPage(DataArray<T> dataArray) {
    mPerPage = dataArray.perPage();
    mCurrentPage = dataArray.currentPage();
    mTotalSize = dataArray.total();

    mHasMore = mTotalSize > mPerPage * mCurrentPage;
  }

  @SuppressWarnings("unchecked")
  @Override protected Call<DataArray<T>> paginate(boolean isRefresh) {
    final PagesEmitter<T> emitter = (PagesEmitter<T>) mEmitter;
    if (emitter != null) {
      return (Call<DataArray<T>>) emitter.paginate(isRefresh ? mFirstPage : (currentPage() + 1), perPage());
    }
    return null;
  }

  public static class Builder<T extends Entitiy> {
    private PagesEmitter<T> emitter;
    private PaginationCallback<T> callback;

    private int firstPage;
    private int perPage;

    /** Create the {@link PagesPaginator} instances. */
    public PagesPaginator<T> build() {
      if (callback == null) {
        throw new IllegalArgumentException("PaginationCallback may not be null.");
      }
      if (emitter == null) {
        throw new IllegalArgumentException("Emitter may not be null.");
      }
      ensureSaneDefaults();
      return new PagesPaginator<>(emitter, callback, firstPage, perPage);
    }

    private void ensureSaneDefaults() {
      if (perPage <= 0) {
        perPage = DEFAULT_PER_PAGE;
      }
    }

    public Builder<T> emitter(PagesEmitter<T> emitter) {
      this.emitter = emitter;
      return this;
    }

    public Builder<T> callback(PaginationCallback<T> callback) {
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
