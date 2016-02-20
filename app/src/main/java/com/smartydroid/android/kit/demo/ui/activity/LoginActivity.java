package com.smartydroid.android.kit.demo.ui.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.kit.demo.api.ApiService;
import com.smartydroid.android.kit.demo.api.service.AuthService;
import com.smartydroid.android.kit.demo.model.entity.User;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import retrofit2.Call;

/**
 * Created by YuGang Yang on February 20, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public class LoginActivity extends StarterNetworkActivity<User> {

  @Bind(R.id.container_login_username) TextInputLayout mUsernameContainer;
  @Bind(R.id.container_login_password) TextInputLayout mPasswordContainer;
  EditText mUsernameEdit;
  EditText mPasswordEdit;

  private AuthService mAuthService;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAuthService = ApiService.createAuthService();

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
    if (s.length() < 6) {
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
    if (s.length() < 6) {
      mPasswordContainer.setErrorEnabled(true);
      mPasswordContainer.setError(
          StarterKitApp.appResources().getString(R.string.login_passowrd_error));
    } else {
      mPasswordContainer.setErrorEnabled(false);
    }
  }

  @OnClick({ R.id.btn_login, R.id.container_register }) public void onClick(View view) {
    hideSoftInputMethod();
    final String username = mUsernameEdit.getText().toString();
    final String password = mPasswordEdit.getText().toString();

    Call<User> userCall = mAuthService.login(username, password);
    networkQueue().enqueue(userCall);
  }

  @Override public void startRequest() {
    super.startRequest();
    showProgressLoading("正在登录...");
  }

  @Override public void respondSuccess(User data) {
    super.respondSuccess(data);
    AccountManager.store(data);
    Snackbar.make(getWindow().getDecorView(), "登录成功", Snackbar.LENGTH_SHORT).show();
  }

  @Override public void endRequest() {
    super.endRequest();
    dismissProgressLoading();
  }

}
