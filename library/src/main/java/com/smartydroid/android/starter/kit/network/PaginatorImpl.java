/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.contracts.Pagination.Paginator;
import com.smartydroid.android.starter.kit.model.dto.DataArray;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.callback.PaginationCallback;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public abstract class PaginatorImpl<T extends Entitiy>
    implements Paginator<T>, Callback<DataArray<T>> {

  static final int DEFAULT_PER_PAGE = 20;

  int mPerPage;

  boolean mHasMore;
  boolean mIsLoading = false;
  boolean mDataHasLoaded = false;
  boolean mHasError = false;

  final LinkedHashMap<Object, T> mResources = new LinkedHashMap<>();

  protected Emitter<T> mEmitter;
  private PaginationCallback<T> mCallback;
  private LoadStyle mLoadStyle = LoadStyle.REFRESH;

  private Call<DataArray<T>> mCall;

  protected PaginatorImpl(Emitter<T> emitter, PaginationCallback<T> callback, int perPage) {
    mEmitter = emitter;
    mCallback = callback;
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

    onStart();

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

    onStart();

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

        respondSuccess(dataArray);
      } else {
        respondWithError(dataArray);
      }
    } else {
      respondWithError(new Throwable(response.message()));
    }
    onFinish();
  }

  @Override public void onFailure(Throwable t) {
    mIsLoading = false;
    mDataHasLoaded = true;
    mHasError = true;

    respondWithError(t);

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

  /**
   * 成功
   */
  private void respondSuccess(DataArray<T> dataArray) {
    mHasError = false;
    if (mCallback != null) {
      mCallback.respondSuccess(dataArray);
    }
  }

  private void respondWithError(DataArray<T> dataArray) {
    if (mCallback != null) {
      mCallback.respondWithError(new Throwable());
    }
  }

  /**
   * 错误
   */
  private void respondWithError(Throwable t) {
    if (mCallback != null) {
      if (t instanceof SocketTimeoutException) {
        // 网络链接超时
        // TODO
      }
      mCallback.respondWithError(t);
    }
  }

  /**
   * 处理完毕
   */
  private void onFinish() {
    if (mCallback != null) {
      mCallback.onFinish();
    }
  }

  private void onStart() {
    if (mCallback != null) {
      mCallback.onStart();
    }
  }
}
