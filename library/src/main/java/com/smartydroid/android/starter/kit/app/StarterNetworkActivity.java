/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import com.smartydroid.android.starter.kit.retrofit.NetworkQueue;

public abstract class StarterNetworkActivity<T> extends StarterActivity implements GenericCallback<T> {

  private NetworkQueue<T> networkQueue;
  public abstract View provideSnackbarView();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    networkQueue = new NetworkQueue<>(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    networkQueue.cancel();
    networkQueue = null;
  }

  public NetworkQueue<T> networkQueue() {
    return networkQueue;
  }

  public boolean showMessage() {
    return true;
  }

  @Override public void errorNotFound(ErrorModel errorModel) {
  }

  @Override public void errorUnprocessable(ErrorModel errorModel) {
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
  }

  @Override public void errorForbidden(ErrorModel errorModel) {
  }

  @Override public void eNetUnreach(Throwable t) {

  }

  @Override public void error(ErrorModel errorModel) {
    if (showMessage() && errorModel != null && provideSnackbarView() != null) {
      Snackbar.make(provideSnackbarView(),
          errorModel.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
  }

  @Override public void startRequest() {
  }

  @Override public void respondSuccess(T data) {
  }

  @Override public void respondWithError(Throwable t) {
  }

  @Override public void endRequest() {
  }

}
