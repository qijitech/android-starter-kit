/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.model.entity.Entitiy;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public abstract class Paginator<T extends Entitiy>
    implements com.smartydroid.android.starter.kit.contracts.Pagination.Paginator<T>,
    Callback<ArrayList<T>> {

  static final int DEFAULT_PER_PAGE = 20;
  static final int DEFAULT_FIRST_PAGE = 1;

  int mPerPage;

  boolean mHasMore;
  boolean mIsLoading = false;
  boolean mDataHasLoaded = false;
  boolean mHasError = false;

  final LinkedHashMap<Object, T> mResources = new LinkedHashMap<>();

  protected Emitter<T> mEmitter;
  private PaginatorCallback<T> mCallback;
  private LoadStyle mLoadStyle = LoadStyle.REFRESH;

  private Call<ArrayList<T>> mCall;

  protected Paginator(Emitter<T> emitter, PaginatorCallback<T> callback, int perPage) {
    mEmitter = emitter;
    mCallback = callback;
    mPerPage = perPage;
  }

  protected abstract Call<ArrayList<T>> paginate(boolean isRefresh);
  protected abstract void processPage(ArrayList<T> dataArray);

  @Override public List<T> items() {
    return new ArrayList<>(mResources.values());
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

    onStart(true);

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

    onStart(false);

    if (mCall != null) {
      mCall.enqueue(this);
    }
  }

  @Override public void onResponse(Response<ArrayList<T>> response, Retrofit retrofit) {
    mIsLoading = false;
    mDataHasLoaded = true;
    if (response.isSuccess()) {
      final ArrayList<T> dataArray = response.body();
      if (!isNull(dataArray)) {
        processPage(dataArray);
        handDataArray(dataArray);
        respondSuccess(dataArray);
      } else {
        mHasMore = false;
        respondWithError(dataArray);
      }
    } else {
      if (response.code() == 400) {
        // Nothing to do
      }
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

  private boolean isNull(ArrayList<T> dataArray) {
    return dataArray == null || dataArray.isEmpty();
  }

  private void handDataArray(ArrayList<T> dataArray) {
    if (isRefresh()) {
      mResources.clear();
    }

    if (!isNull(dataArray)) {
      for (T item : dataArray) {
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
  private void respondSuccess(ArrayList<T> dataArray) {
    mHasError = false;
    if (mCallback != null) {
      mCallback.respondSuccess(dataArray);
    }
  }

  private void respondWithError(ArrayList<T> dataArray) {
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

  private void onStart(boolean isRefresh) {
    if (mCallback != null) {
      mCallback.onStart(isRefresh);
    }
  }
}
