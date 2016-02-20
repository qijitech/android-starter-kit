package com.smartydroid.android.kit.demo.ui.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.app.StarterKitApp;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public class LoginActivity extends StarterActivity {

  @Bind(R.id.container_login_username) TextInputLayout mUsernameContainer;
  @Bind(R.id.container_login_password) TextInputLayout mPasswordContainer;
  EditText mUsernameEdit;
  EditText mPasswordEdit;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    Resources resources = StarterKitApp.appResources();
    mUsernameContainer.setHint(resources.getString(R.string.login_username_hint));
    mPasswordContainer.setHint(resources.getString(R.string.login_passowrd_hint));
    setupViews();
  }

  private void setupViews() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setElevation(0);
      actionBar.setDisplayShowTitleEnabled(false);
    }
    mUsernameEdit = mUsernameContainer.getEditText();
    mPasswordEdit = mPasswordContainer.getEditText();
  }

  @OnTextChanged(
      R.id.edit_login_username)
  public void onUsernameTextChanged(CharSequence s, int start, int before, int count) {
    if (s.length() <= 6) {
      mUsernameContainer.setErrorEnabled(true);
      mUsernameContainer.setError(
          StarterKitApp.appResources().getString(R.string.login_username_error));
    } else {
      mUsernameContainer.setErrorEnabled(false);
    }
  }

  @OnTextChanged(
      R.id.edit_login_password)
  public void onPasswordTextChanged(CharSequence s, int start, int before, int count) {
    if (s.length() <= 6) {
      mPasswordContainer.setErrorEnabled(true);
      mPasswordContainer.setError(
          StarterKitApp.appResources().getString(R.string.login_passowrd_error));
    } else {
      mPasswordContainer.setErrorEnabled(false);
    }
  }

  @OnClick({ R.id.btn_login, R.id.container_register }) public void onClick(View view) {
    hideSoftInputMethod();
  }
}
