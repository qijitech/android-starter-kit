/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.smartydroid.android.starter.kit.utilities.Utils.checkNotNull;

public class NetworkQueue<T> implements Callback<T> {

  private final GenericCallback<T> callback;
  private Call<T> delegate;

  public NetworkQueue(GenericCallback<T> callback) {
    checkNotNull(callback, "callback == null");
    this.callback = callback;
  }

  public void enqueue(Call<T> delegate) {
    checkNotNull(delegate, "delegate == null");
    this.delegate = delegate;
    callback.startRequest();
    delegate.enqueue(this);
  }

  public void cancel() {
    if (delegate != null) {
      delegate.cancel();
    }
  }

  @Override public void onResponse(Response<T> response, Retrofit retrofit) {
    if (response.isSuccess()) {
      callback.respondSuccess(response.body());
    } else {
      final int statusCode = response.code();
      final ResponseBody errorBody = response.errorBody();
      ErrorModel errorModel = null;
      if (errorBody != null) {
        try {
          ObjectMapper mapper = new ObjectMapper();
          String json = errorBody.string();
          errorModel = mapper.readValue(json, ErrorModel.class);
        } catch (IOException e) {
          // Nothing to do
        }
      }
      processError(statusCode, errorModel);
    }

    callback.endRequest();
  }

  @Override public void onFailure(Throwable t) {
    if (t instanceof ConnectException) { // 无网络
      callback.eNetUnreach(t);
    } else if (t instanceof SocketTimeoutException) { // 链接超时
      callback.errorSocketTimeout(t);
    } else {
      callback.respondWithError(t);
    }
    callback.endRequest();
  }

  private void processError(final int statusCode, ErrorModel errorModel) {
    switch (statusCode) {
      case 401:
        callback.errorUnauthorized(errorModel);
        break;
      case 403:
        callback.errorForbidden(errorModel);
        break;
      case 404:
        callback.errorNotFound(errorModel);
        break;
      case 422:
        callback.errorUnprocessable(errorModel);
        break;
    }
    callback.error(errorModel);
  }
}
