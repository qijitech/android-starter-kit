/**
 * Created by YuGang Yang on October 28, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network;

import com.smartydroid.android.starter.kit.contracts.Pagination.Emitter;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.model.entity.Entity;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import com.smartydroid.android.starter.kit.network.callback.PaginatorCallback;
import com.smartydroid.android.starter.kit.retrofit.NetworkQueue;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import retrofit.Call;

import static com.smartydroid.android.starter.kit.utilities.Utils.checkNotNull;

public abstract class Paginator<T extends Entity>
    implements com.smartydroid.android.starter.kit.contracts.Pagination.Paginator<T>,
    GenericCallback<ArrayList<T>> {

  static final int DEFAULT_PER_PAGE = 20;
  static final int DEFAULT_FIRST_PAGE = 1;

  int mPerPage;

  boolean mHasMore;
  boolean mIsLoading = false;
  boolean mDataHasLoaded = false;
  boolean mHasError = false;

  final LinkedHashMap<Object, T> mResources = new LinkedHashMap<>();

  protected Emitter<T> mEmitter;
  private PaginatorCallback<T> delegate;
  private LoadStyle mLoadStyle = LoadStyle.REFRESH;

  private NetworkQueue<ArrayList<T>> networkQueue;

  protected Paginator(Emitter<T> emitter, PaginatorCallback<T> delegate, int perPage) {
    checkNotNull(emitter, "emitter == null");
    checkNotNull(delegate, "delegate == null");

    mEmitter = emitter;
    this.delegate = delegate;
    mPerPage = perPage;

    networkQueue = new NetworkQueue<>(this);
  }

  protected abstract Call<ArrayList<T>> paginate();
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
    return !isLoading() && hasMorePages() && !hasError();
  }

  @Override public boolean isRefresh() {
    return mLoadStyle == LoadStyle.REFRESH;
  }

  @Override public boolean isLoading() {
    return mIsLoading;
  }

  @Override public void cancel() {
    mIsLoading = false;
    networkQueue.cancel();
  }

  @Override public void refresh() {
    if (mIsLoading) return;
    mLoadStyle = LoadStyle.REFRESH;
    mEmitter.beforeRefresh();
    requestData();
  }

  private void requestData() {
    final Call<ArrayList<T>> call = paginate();
    networkQueue.enqueue(call);
  }

  @Override public void loadMore() {
    if (mIsLoading) return;
    mLoadStyle = LoadStyle.LOAD_MORE;
    mEmitter.beforeLoadMore();
    requestData();
  }

  @Override public void respondSuccess(ArrayList<T> data) {
    if (!isNull(data)) {
      mHasError = false;
      processPage(data);
      handDataArray(data);

      delegate.respondSuccess(data);
    } else {
      mHasMore = false;
      mHasError = true;
      delegate.errorNotFound(new ErrorModel(404, "no more"));
    }
  }

  @Override public void startRequest() {
    mIsLoading = true;
    delegate.startRequest();
  }

  @Override public void endRequest() {
    mIsLoading = false;
    mDataHasLoaded = true;
    delegate.endRequest();
  }

  @Override public void errorNotFound(ErrorModel errorModel) {
    delegate.errorNotFound(errorModel);
  }

  @Override public void errorUnprocessable(ErrorModel errorModel) {
    setupError();
    delegate.errorUnprocessable(errorModel);
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
    setupError();
    delegate.errorUnauthorized(errorModel);
  }

  @Override public void errorForbidden(ErrorModel errorModel) {
    setupError();
    delegate.errorForbidden(errorModel);
  }

  @Override public void eNetUnreach(Throwable t) {
    setupError();
    delegate.eNetUnreach(t);
  }

  @Override public void errorSocketTimeout(Throwable t) {
    setupError();
    delegate.errorSocketTimeout(t);
  }

  @Override public void EAI_NODATA(UnknownHostException e) {
    setupError();
    delegate.EAI_NODATA(e);
  }

  @Override public void error(ErrorModel errorModel) {
    setupError();
    delegate.error(errorModel);
  }

  private void setupError() {
    mHasError = true;
    mHasMore = false;
  }

  @Override public void respondWithError(Throwable t) {
    delegate.respondWithError(t);
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

}
