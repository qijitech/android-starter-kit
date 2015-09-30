/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.IdEmitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.IdPaginator;
import com.smartydroid.android.starter.kit.contracts.Pagination.PageCallback;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import retrofit.Call;
import retrofit.Response;

public class KeyPaginator<T extends Entitiy> implements IdPaginator<T> {

  private static final int DEFAULT_PER_PAGE = 20;

  int mPerPage;
  boolean mHasMore;
  boolean mIsLoading = false;
  boolean mDataHasLoaded = false;
  boolean mHasError = false;

  private T nextItem;
  private T previousItem;

  final LinkedHashMap<Object, T> mResources = new LinkedHashMap<>();

  private IdEmitter<T> mEmitter;
  private PageCallback<T> mPageCallback;
  private LoadStyle mLoadStyle = LoadStyle.REFRESH;

  private Call<DataArray<T>> mCall;
  public enum LoadStyle {
    REFRESH,
    LOAD_MORE,
  }

  public static class Builder<T extends Entitiy> {
    private IdEmitter<T> emitter;
    private PageCallback<T> pageCallback;

    private int perPage;

    /** Create the {@link KeyPaginator} instances. */
    public KeyPaginator<T> build() {
      if (pageCallback == null) {
        throw new IllegalArgumentException("PageCallback may not be null.");
      }
      if (emitter == null) {
        throw new IllegalArgumentException("Emitter may not be null.");
      }
      ensureSaneDefaults();
      return new KeyPaginator<>(emitter, pageCallback, perPage);
    }

    private void ensureSaneDefaults() {
      if (perPage <= 0) {
        perPage = DEFAULT_PER_PAGE;
      }
    }

    public Builder<T> setEmitter(IdEmitter<T> emitter) {
      this.emitter = emitter;
      return this;
    }

    public Builder<T> setPageCallback(PageCallback<T> pageCallback) {
      this.pageCallback = pageCallback;
      return this;
    }

    public Builder<T> setPerPage(int perPage) {
      this.perPage = perPage;
      return this;
    }
  }

  private KeyPaginator(IdEmitter<T> emitter, PageCallback<T> pageCallback, int perPage) {
    mEmitter = emitter;
    mPageCallback = pageCallback;
    mPerPage = perPage;
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

  @Override public T previousPageItem() {
    return null;
  }

  @Override public T nextPageItem() {
    return null;
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

    mCall = (Call<DataArray<T>>) mEmitter.paginate(lastItem(), fisrtItem(), perPage());
    mCall.enqueue(this);
  }

  @Override public void loadMore() {
    if (mIsLoading) return;
    mIsLoading = true;
    mLoadStyle = LoadStyle.LOAD_MORE;
    mEmitter.beforeLoadMore();
    mCall = (Call<DataArray<T>>) mEmitter.paginate(previousItem, nextItem, perPage());
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

    mHasMore = dataArray.size() >= mPerPage;

    if (isRefresh()) {
      mResources.clear();
    }

    if (!dataArray.isNull()) {
      final List<T> items = dataArray.data();

      nextItem = items.get(items.size() - 1);
      previousItem = items.get(0);

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
