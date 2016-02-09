/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

  @Override public void onResponse(Call<T> call, Response<T> response) {
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
          errorModel = new ErrorModel(statusCode, e.getMessage());
        }
      }
      processError(statusCode, errorModel);
    }

    callback.endRequest();
  }

  @Override public void onFailure(Call<T> call, Throwable t) {
    if (t instanceof ConnectException) { // 无网络
      ConnectException e = (ConnectException) t;
      callback.eNetUnReach(t, new ErrorModel(500, e.getLocalizedMessage()));
    } else if (t instanceof SocketTimeoutException) { // 链接超时
      final SocketTimeoutException e = (SocketTimeoutException) t;
      callback.errorSocketTimeout(t, new ErrorModel(500, e.getLocalizedMessage()));
    } else if (t instanceof UnknownHostException) {
      final UnknownHostException e = (UnknownHostException) t;
      ErrorModel errorModel = new ErrorModel(500, e.getLocalizedMessage());
      callback.errorUnknownHost(e, errorModel);
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
        callback.errorUnProcessable(errorModel);
        break;
      default:
        callback.error(errorModel);
        break;
    }
  }
}
