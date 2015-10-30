/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import com.smartydroid.android.starter.kit.model.ErrorModel;

public class SimpleCallback<T> implements GenericCallback<T> {

  @Override public void startRequest() {
  }

  @Override public void respondSuccess(T data) {
  }

  @Override public void respondWithError(Throwable t) {
  }

  @Override public void endRequest() {
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

  @Override public void errorSocketTimeout(Throwable t) {

  }

  @Override public void error(ErrorModel errorModel) {

  }
}
