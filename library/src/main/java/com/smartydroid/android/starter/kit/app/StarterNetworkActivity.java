/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import com.smartydroid.android.starter.kit.model.ErrorModel;
import com.smartydroid.android.starter.kit.network.callback.GenericCallback;
import com.smartydroid.android.starter.kit.retrofit.NetworkQueue;
import java.net.UnknownHostException;

public abstract class StarterNetworkActivity<T> extends StarterActivity implements GenericCallback<T> {

  private NetworkQueue<T> networkQueue;

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
    showErrorModel(errorModel);
  }

  @Override public void errorUnProcessable(ErrorModel errorModel) {
    showErrorModel(errorModel);
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
    showErrorModel(errorModel);
  }

  @Override public void errorForbidden(ErrorModel errorModel) {
    showErrorModel(errorModel);
  }

  @Override public void eNetUnReach(Throwable t, ErrorModel errorModel) {
    showException(t);
  }

  @Override public void errorSocketTimeout(Throwable t, ErrorModel errorModel) {
    showException(t);
  }

  @Override public void errorUnknownHost(UnknownHostException e, ErrorModel errorModel) {
    showException(e);
  }

  @Override public void error(ErrorModel errorModel) {
    showErrorModel(errorModel);
  }

  private void showErrorModel(ErrorModel errorModel) {
    if (showMessage() && errorModel != null) {
      Snackbar.make(getWindow().getDecorView(),
          errorModel.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
  }

  private void showException(Throwable e) {
    if (showMessage() && e != null) {
      Snackbar.make(getWindow().getDecorView(),
          e.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
  }

  @Override public void startRequest() {
    showHud();
  }

  @Override public void respondSuccess(T data) {
  }

  @Override public void respondWithError(Throwable t) {
    showException(t);
  }

  @Override public void endRequest() {
    dismissHud();
  }

}
