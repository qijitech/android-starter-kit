package com.smartydroid.android.kit.demo.ui.activity;

import android.os.Bundle;
import android.widget.TextView;
import butterknife.Bind;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.kit.demo.api.ApiService;
import com.smartydroid.android.kit.demo.api.service.AuthService;
import com.smartydroid.android.kit.demo.model.entity.User;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import retrofit2.Call;

/**
 * Created by YuGang Yang on February 26, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public class AccountProfileActivity extends StarterNetworkActivity<User> {

  @Bind(R.id.text_account_username) TextView mUsernameTextView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    AuthService authService = ApiService.createAuthService();
    Call<User> userCall = authService.profile();

    networkQueue().enqueue(userCall);
  }

  @Override public void respondSuccess(User data) {
    super.respondSuccess(data);
    mUsernameTextView.setText(data.phone);
  }
}
