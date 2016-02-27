package com.smartydroid.android.kit.demo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.facebook.drawee.view.SimpleDraweeView;
import com.smartydroid.android.kit.demo.NavUtils;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.kit.demo.model.entity.User;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

/**
 * Created by YuGang Yang on February 21, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class AccountFragment extends BaseFragment {

  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.container_login) View mLoginContainer;
  @Bind(R.id.container_account) View mUserInfoContainer;
  @Bind(R.id.image_avatar) SimpleDraweeView mAvatarView;
  @Bind(R.id.text_account_username) TextView mUsernameTextView;

  @Override protected int getFragmentLayout() {
    return R.layout.fragment_account;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mToolbar.setTitle(R.string.toolbar_title_account);
  }

  @Override public void onResume() {
    super.onResume();
    if (AccountManager.isLogin()) {
      ViewUtils.setGone(mLoginContainer, true);
      ViewUtils.setGone(mUserInfoContainer, false);
      User user = AccountManager.getCurrentAccount();
      mUsernameTextView.setText(user.phone);
      mAvatarView.setImageURI(user.uri());
    } else {
      ViewUtils.setGone(mLoginContainer, false);
      ViewUtils.setGone(mUserInfoContainer, true);
    }
  }

  @OnClick({
      R.id.container_account_settings,
  }) public void onClick(View view) {
    NavUtils.startAccountProfile(getActivity());
  }
}
