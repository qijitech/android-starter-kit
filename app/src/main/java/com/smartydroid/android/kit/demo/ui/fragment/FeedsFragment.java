package com.smartydroid.android.kit.demo.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import butterknife.Bind;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YuGang Yang on February 21, 2016.
 * Copyright 20015-2016 qiji.tech. All rights reserved.
 */
public class FeedsFragment extends BaseFragment {

  @Bind(R.id.view_pager) ViewPager mViewPager;
  @Bind(R.id.tabs) TabLayout mTabLayout;
  @Bind(R.id.toolbar) Toolbar mToolbar;

  private Adapter mAdapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAdapter = new Adapter(getChildFragmentManager());
    Resources r = StarterKitApp.appResources();
    mAdapter.addFragment(FeedsPagedFragment.create(), r.getString(R.string.feed_with_page));
    mAdapter.addFragment(FeedsKeyFragment.create(), r.getString(R.string.feed_with_id));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mToolbar.setTitle(R.string.app_name);
    mViewPager.setAdapter(mAdapter);

    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
        StarterKitApp.appResources().getDisplayMetrics());
    mViewPager.setPageMargin(pageMargin);
    mTabLayout.setupWithViewPager(mViewPager);
  }

  // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
  @Override public void onDetach() {
    super.onDetach();
    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override protected int getFragmentLayout() {
    return R.layout.fragment_home;
  }

  static class Adapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public Adapter(FragmentManager fm) {
      super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
      mFragments.add(fragment);
      mFragmentTitles.add(title);
    }

    @Override public Fragment getItem(int position) {
      return mFragments.get(position);
    }

    @Override public int getCount() {
      return mFragments.size();
    }

    @Override public CharSequence getPageTitle(int position) {
      return mFragmentTitles.get(position);
    }
  }

}
