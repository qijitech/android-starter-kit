/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import com.smartydroid.android.starter.kit.model.ErrorModel;
import java.net.UnknownHostException;

import static com.smartydroid.android.starter.kit.utilities.Utils.checkNotNull;

public class DecorCallback<T> implements GenericCallback<T> {

  private final GenericCallback<T> delegate;

  public DecorCallback(GenericCallback<T> delegate) {
    checkNotNull(delegate, "delegate == null");
    this.delegate = delegate;
  }

  @Override public void errorNotFound(ErrorModel errorModel) {
    delegate.errorNotFound(errorModel);
  }

  @Override public void errorUnProcessable(ErrorModel errorModel) {
    delegate.errorUnProcessable(errorModel);
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
    delegate.errorUnauthorized(errorModel);
  }

  @Override public void errorForbidden(ErrorModel errorModel) {
    delegate.errorForbidden(errorModel);
  }

  @Override public void eNetUnReach(Throwable t, ErrorModel errorModel) {
    delegate.eNetUnReach(t, errorModel);
  }

  @Override public void errorSocketTimeout(Throwable t, ErrorModel errorModel) {
    delegate.errorSocketTimeout(t, errorModel);
  }

  @Override public void errorUnknownHost(UnknownHostException e, ErrorModel errorModel) {
    delegate.errorUnknownHost(e, errorModel);
  }

  @Override public void error(ErrorModel errorModel) {
    delegate.error(errorModel);
  }

  @Override public void startRequest() {
    delegate.startRequest();
  }

  @Override public void respondSuccess(T data) {
    delegate.respondSuccess(data);
  }

  @Override public void respondWithError(Throwable t) {
    delegate.respondWithError(t);
  }

  @Override public void endRequest() {
    delegate.endRequest();
  }
}
