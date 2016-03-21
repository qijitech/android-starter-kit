package com.smartydroid.android.kit.demo.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.smartydroid.android.kit.demo.NavUtils;
import com.smartydroid.android.starter.kit.model.ErrorModel;

/**
 * Created by YuGang Yang on February 27, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class MessageCallback<T> extends
    com.smartydroid.android.starter.kit.network.callback.MessageCallback<T> {

  public MessageCallback(Activity activity) {
    super(activity);
  }

  @Override public void errorUnauthorized(ErrorModel errorModel) {
    super.errorUnauthorized(errorModel);
    DialogUtils.showLoginDialog(getActivity(), new MaterialDialog.SingleButtonCallback() {
      @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        if (getActivity() != null && !getActivity().isFinishing()) {
          NavUtils.startLogin(getActivity(), NavUtils.ACCOUNT_PROFILE_ACTION);
        }
      }
    }, new MaterialDialog.SingleButtonCallback() {
      @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        if (getActivity() != null && !getActivity().isFinishing()) {
          getActivity().finish();
        }
      }
    });
  }
}
