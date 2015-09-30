/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.PageCallback;
import com.smartydroid.android.starter.kit.contracts.Pagination.PagePaginator;
import com.smartydroid.android.starter.kit.contracts.Pagination.PagesEmitter;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import retrofit.Call;

public class PagesPaginator<T extends Entitiy> extends PaginatorImpl<T>
    implements PagePaginator<T> {

  private static final int DEFAULT_START_PAGE = 1;
  int mStartPage = DEFAULT_START_PAGE;

  int mCurrentPage;
  long mTotalSize;

  private PagesPaginator(PagesEmitter<T> emitter, PageCallback<T> pageCallback, int startPage,
      int perPage) {
    super(emitter, pageCallback, perPage);
    mStartPage = startPage;
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

  @Override protected Call<DataArray<T>> paginate(boolean isRefresh) {
    final PagesEmitter<T> emitter = (PagesEmitter<T>) mEmitter;
    if (emitter != null) {
      return (Call<DataArray<T>>) emitter.paginate(isRefresh ? mStartPage : (currentPage() + 1), perPage());
    }
    return null;
  }

  public static class Builder<T extends Entitiy> {
    private PagesEmitter<T> emitter;
    private PageCallback<T> pageCallback;

    private int startPage;
    private int perPage;

    /** Create the {@link PagesPaginator} instances. */
    public PagesPaginator<T> build() {
      if (pageCallback == null) {
        throw new IllegalArgumentException("PageCallback may not be null.");
      }
      if (emitter == null) {
        throw new IllegalArgumentException("Emitter may not be null.");
      }
      ensureSaneDefaults();
      return new PagesPaginator<>(emitter, pageCallback, startPage, perPage);
    }

    private void ensureSaneDefaults() {
      if (perPage <= 0) {
        perPage = DEFAULT_PER_PAGE;
      }
    }

    public Builder<T> setEmitter(PagesEmitter<T> emitter) {
      this.emitter = emitter;
      return this;
    }

    public Builder<T> setPageCallback(PageCallback<T> pageCallback) {
      this.pageCallback = pageCallback;
      return this;
    }

    public Builder<T> setStartPage(int startPage) {
      this.startPage = startPage;
      return this;
    }

    public Builder<T> setPerPage(int perPage) {
      this.perPage = perPage;
      return this;
    }
  }
}
