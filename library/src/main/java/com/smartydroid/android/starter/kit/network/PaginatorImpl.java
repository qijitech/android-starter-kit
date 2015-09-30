/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.PageCallback;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import retrofit.Call;
import retrofit.Response;

public abstract class PaginatorImpl<T extends Entitiy> implements Paginator<T> {

  static final int DEFAULT_PER_PAGE = 20;

  int mPerPage;

  boolean mHasMore;
  boolean mIsLoading = false;
  boolean mDataHasLoaded = false;
  boolean mHasError = false;

  final LinkedHashMap<Object, T> mResources = new LinkedHashMap<>();

  protected Emitter<T> mEmitter;
  private PageCallback<T> mPageCallback;
  private LoadStyle mLoadStyle = LoadStyle.REFRESH;

  private Call<DataArray<T>> mCall;

  protected PaginatorImpl(Emitter<T> emitter, PageCallback<T> pageCallback, int perPage) {
    mEmitter = emitter;
    mPageCallback = pageCallback;
    mPerPage = perPage;
  }

  protected abstract Call<DataArray<T>> paginate(boolean isRefresh);

  protected abstract void processPage(DataArray<T> dataArray);

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
    mCall = paginate(true);
    if (mCall != null) {
      mCall.enqueue(this);
    }
  }

  @Override public void loadMore() {
    if (mIsLoading) return;
    mIsLoading = true;
    mLoadStyle = LoadStyle.LOAD_MORE;
    mEmitter.beforeLoadMore();
    mCall = paginate(false);
    if (mCall != null) {
      mCall.enqueue(this);
    }
  }

  @Override public void onResponse(Response<DataArray<T>> response) {
    mIsLoading = false;
    mDataHasLoaded = true;
    if (response.isSuccess()) {
      final DataArray<T> dataArray = response.body();
      if (dataArray.isSuccess()) {
        processPage(dataArray);
        handDataArray(dataArray);
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

  private void handDataArray(DataArray<T> dataArray) {

    if (isRefresh()) {
      mResources.clear();
    }

    if (!dataArray.isNull()) {
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
}
