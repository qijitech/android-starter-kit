package com.smartydroid.android.kit.demo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.smartydroid.android.kit.demo.NavUtils;
import com.smartydroid.android.kit.demo.ui.fragment.KeyFeedsFragment;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.SimpleSinglePaneActivity;

/**
 * Created by YuGang Yang on February 13, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public class KeysActivity extends SimpleSinglePaneActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!AccountManager.isLogin()) {
      NavUtils.startLogin(this);
    }
  }

  @Override protected Fragment onCreatePane() {
    return new KeyFeedsFragment();
  }
}
