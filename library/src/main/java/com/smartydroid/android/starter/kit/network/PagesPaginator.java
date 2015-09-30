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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import retrofit.Call;
import retrofit.Response;

public class PagesPaginator<T extends Entitiy> implements PagePaginator<T> {

  private static final int DEFAULT_START_PAGE = 1;
  private static final int DEFAULT_PER_PAGE = 20;

  int mStartPage = DEFAULT_START_PAGE;
  int mPerPage;
  int mCurrentPage;
  long mTotalSize;
  boolean mHasMore;
  boolean mIsLoading = false;
  boolean mDataHasLoaded = false;
  boolean mHasError = false;

  final LinkedHashMap<Object, T> mResources = new LinkedHashMap<>();

  private PagesEmitter<T> mEmitter;
  private PageCallback<T> mPageCallback;
  private LoadStyle mLoadStyle = LoadStyle.REFRESH;

  private Call<DataArray<T>> mCall;

  public enum LoadStyle {
    REFRESH,
    LOAD_MORE,
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

  private PagesPaginator(PagesEmitter<T> emitter, PageCallback<T> pageCallback, int startPage,
      int perPage) {
    mEmitter = emitter;
    mPageCallback = pageCallback;
    mPerPage = perPage;
    mStartPage = startPage;
  }

  @Override public List<T> items() {
    return new ArrayList<>(mResources.values());
  }

  @Override public T fisrtItem() {
    final List<T> items = items();
    return items.isEmpty() ? null : items.get(0);
  }

  @Override public T lastItem() {
    final List<T> items = items();
    return items.isEmpty() ? null : items.get(items.size() - 1);
  }

  @Override public int perPage() {
    return mPerPage;
  }

  @Override public int currentPage() {
    return mCurrentPage;
  }

  @Override public boolean hasMorePages() {
    return mHasMore;
  }

  @Override public boolean isEmpty() {
    return mResources.isEmpty();
  }

  @Override public boolean hasError() {
    return mHasError;
  }

  @Override public boolean dataHasLoaded() {
    return mDataHasLoaded;
  }

  @Override public boolean canLoadMore() {
    return !isLoading() && hasMorePages();
  }

  @Override public boolean isRefresh() {
    return mLoadStyle == LoadStyle.REFRESH;
  }

  @Override public boolean isLoading() {
    return mIsLoading;
  }

  @Override public void cancel() {
    mIsLoading = false;
    if (mCall != null) {
      mCall.cancel();
      mCall = null;
    }
  }

  @Override public void refresh() {
    if (mIsLoading) return;
    mIsLoading = true;
    mLoadStyle = LoadStyle.REFRESH;
    mEmitter.beforeRefresh();
    mCall = (Call<DataArray<T>>) mEmitter.paginate(mStartPage, perPage());
    mCall.enqueue(this);
  }

  @Override public void loadMore() {
    if (mIsLoading) return;
    mIsLoading = true;
    mLoadStyle = LoadStyle.LOAD_MORE;
    mEmitter.beforeLoadMore();
    mCall = (Call<DataArray<T>>) mEmitter.paginate(currentPage() + 1, perPage());
    mCall.enqueue(this);
  }

  @Override public void onResponse(Response<DataArray<T>> response) {
    mIsLoading = false;
    mDataHasLoaded = true;
    if (response.isSuccess()) {
      final DataArray<T> dataArray = response.body();
      if (dataArray.isSuccess()) {
        handResult(dataArray);
        onRequestComplete(dataArray);
      } else {
        onRequestFailure(dataArray);
      }
    } else {
      try {
        onRequestComplete(response.code(), response.errorBody().string());
      } catch (IOException e) {
        onRequestFailure(e);
      }
    }
    onFinish();
  }

  @Override public void onFailure(Throwable t) {
    mIsLoading = false;
    mDataHasLoaded = true;
    mHasError = true;
    onRequestFailure(t);
    onFinish();
  }

  private void onRequestComplete(DataArray<T> dataArray) {
    mHasError = false;
    if (mPageCallback != null) {
      mPageCallback.onRequestComplete(dataArray);
    }
  }

  private void onRequestComplete(int code, String error) {
    mHasError = true;
    if (mPageCallback != null) {
      mPageCallback.onRequestComplete(code, error);
    }
  }

  private void onRequestFailure(DataArray<T> dataArray) {
    mHasError = true;
    if (mPageCallback != null) {
      mPageCallback.onRequestFailure(dataArray);
    }
  }

  private void onRequestFailure(Throwable t) {
    mHasError = true;
    if (mPageCallback != null) {
      mPageCallback.onRequestFailure(t);
    }
  }

  private void onFinish() {
    if (mPageCallback != null) {
      mPageCallback.onFinish();
    }
  }

  private void handResult(DataArray<T> dataArray) {
    mPerPage = dataArray.perPage();
    mCurrentPage = dataArray.currentPage();
    mTotalSize = dataArray.total();

    mHasMore = mTotalSize > mPerPage * mCurrentPage;

    if (isRefresh()) {
      mResources.clear();
    }

    if (! dataArray.isNull()) {
      final List<T> items = dataArray.data();
      for (T item : items) {
        item = mEmitter.register(item);
        final Object key = mEmitter.getKeyForData(item);
        if (item != null && key != null) {
          mResources.put(key, item);
        }
      }
    }
  }
}
