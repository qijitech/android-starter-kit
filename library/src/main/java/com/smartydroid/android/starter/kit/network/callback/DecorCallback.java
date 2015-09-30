/**
 * Created by YuGang Yang on September 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import retrofit.Callback;
import retrofit.Response;

public abstract class DecorCallback<T> implements NetworkCallback<T>, Callback<T> {

  @Override public void onResponse(Response<T> response) {

    respondSuccess(response.body());

    onFinish();
  }

  @Override public void onFailure(Throwable t) {

    respondWithError(t);

    onFinish();
  }
}
