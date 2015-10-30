/**
 * Created by YuGang Yang on October 30, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.network.callback;

import android.support.design.widget.Snackbar;
import android.view.View;
import com.smartydroid.android.starter.kit.model.ErrorModel;

import static com.smartydroid.android.starter.kit.utilities.Utils.checkNotNull;

public class MessageCallback<T> extends SimpleCallback<T> {

  private final View view;

  public MessageCallback(View view) {
    checkNotNull(view, "view == null");
    this.view = view;
  }

  @Override public void error(ErrorModel errorModel) {
    super.error(errorModel);
    if (errorModel != null) {
      Snackbar.make(view, errorModel.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
  }
}
