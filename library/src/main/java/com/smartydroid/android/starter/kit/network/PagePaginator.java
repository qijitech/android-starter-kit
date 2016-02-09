/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PageEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PagePaginatorContract;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import java.util.ArrayList;
import retrofit2.Call;

public class PagePaginator<T extends Entity> extends Paginator<T>
    implements PagePaginatorContract<T> {

  int mFirstPage = DEFAULT_FIRST_PAGE;

  int mCurrentPage;
  int mNextPage = mCurrentPage;

  private PagePaginator(Emitter<T> emitter, PaginatorCallback<T> callback, int startPage,
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

    if (isRefresh()) {
      mCurrentPage = mFirstPage;
    } else {
      mCurrentPage = mNextPage;
    }

    if (mHasMore) {
      mNextPage = mCurrentPage + 1;
    }
  }

  @Override protected Call<ArrayList<T>> paginate() {
    final PageEmitter<T> emitter = (PageEmitter<T>) mEmitter;
    if (emitter != null) {
      return emitter.paginate(isRefresh() ? mFirstPage : mNextPage, perPage());
    }
    return null;
  }

  public static class Builder<T extends Entity> {
    private PageEmitter<T> emitter;
    private PaginatorCallback<T> callback;

    private int firstPage;
    private int perPage;

    /** Create the {@link PagePaginator} instances. */
    public PagePaginator<T> build() {
      if (callback == null) {
        throw new IllegalArgumentException("PaginationCallback may not be null.");
      }
      if (emitter == null) {
        throw new IllegalArgumentException("Emitter may not be null.");
      }
      ensureSaneDefaults();
      return new PagePaginator<>(emitter, callback, firstPage, perPage);
    }

    private void ensureSaneDefaults() {
      if (perPage <= 0) {
        perPage = DEFAULT_PER_PAGE;
      }

      if (firstPage <= 0) {
        firstPage = DEFAULT_FIRST_PAGE;
      }
    }

    public Builder<T> emitter(PageEmitter<T> emitter) {
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
