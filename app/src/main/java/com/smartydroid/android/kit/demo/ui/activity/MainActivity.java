package com.smartydroid.android.kit.demo.ui.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import com.facebook.drawee.view.SimpleDraweeView;
import com.smartydroid.android.kit.demo.NavUtils;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.kit.demo.model.entity.User;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

/**
 * Created by YuGang Yang on February 21, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class MainActivity extends StarterActivity {

  @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.nav_view) NavigationView navigationView;

  View mAccountContainer;
  View mLoginContainer;

  SimpleDraweeView mAvatarView;
  TextView mUsernameTextView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setSupportActionBar(mToolbar);

    final ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setHomeAsUpIndicator(R.drawable.ic_menu);
      ab.setDisplayHomeAsUpEnabled(true);
    }
    if (navigationView != null) {
      setupDrawerContent(navigationView);
    }

    if (navigationView != null) {
      final View headerView = navigationView.getHeaderView(0);
      if (headerView != null) {
        mAccountContainer = ViewUtils.getView(headerView, R.id.container_account);
        mLoginContainer = ViewUtils.getView(headerView, R.id.container_login);
        mAvatarView = ViewUtils.getView(headerView, R.id.image_avatar);
        mUsernameTextView = ViewUtils.getView(headerView, R.id.text_nav);

        mLoginContainer.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            NavUtils.startLogin(MainActivity.this);
          }
        });
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (AccountManager.isLogin()) {
      ViewUtils.setGone(mLoginContainer, true);
      ViewUtils.setGone(mAccountContainer, false);
      User user = AccountManager.getCurrentAccount();
      mUsernameTextView.setText(user.phone);
      mAvatarView.setImageURI(user.uri());
    } else {
      ViewUtils.setGone(mLoginContainer, false);
      ViewUtils.setGone(mAccountContainer, true);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
              case R.id.nav_tab_bar:
                NavUtils.startTab(MainActivity.this);
                break;
            }
            return true;
          }
        });
  }
}
