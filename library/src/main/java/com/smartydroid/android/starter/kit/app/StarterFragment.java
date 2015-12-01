/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.smartydroid.android.starter.kit.utilities.StarterCommon;

public abstract class StarterFragment extends Fragment {

  private StarterCommon starterCommon;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    starterCommon = new StarterCommon(getActivity());
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(getFragmentLayout(), container, false);
  }

  /**
   * Every fragment has to inflate a layout in the onCreateView method. We have added this method
   * to
   * avoid duplicate all the inflate code in every fragment. You only have to return the layout to
   * inflate in this method when extends StarterFragment.
   */
  protected abstract int getFragmentLayout();

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    starterCommon.onDestroy();
    starterCommon = null;
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      hideSoftInputMethod();
    }
  }

  public boolean viewValid(View view) {
    return getActivity() != null && !isDetached() && view != null;
  }

  public boolean isProgressShow() {
    return starterCommon != null && starterCommon.isProgressShow();
  }

  public void showProgressLoading(int resId) {
    if (starterCommon == null) {
      return;
    }
    showProgressLoading(getString(resId));
  }

  public void showProgressLoading(String text) {
    if (starterCommon == null) {
      return;
    }
    starterCommon.showProgressLoading(text);
  }

  public void dismissProgressLoading() {
    if (starterCommon == null) {
      return;
    }
    starterCommon.dismissProgressLoading();
  }

  public void showUnBackProgressLoading(int resId) {
    if (starterCommon == null) {
      return;
    }
    showUnBackProgressLoading(getString(resId));
  }

  public void showUnBackProgressLoading(String text) {
    if (starterCommon == null) {
      return;
    }
    starterCommon.showUnBackProgressLoading(text);
  }

  public void dismissUnBackProgressLoading() {
    if (starterCommon == null) {
      return;
    }
    starterCommon.dismissUnBackProgressLoading();
  }

  public void hideSoftInputMethod() {
    if (starterCommon == null) {
      return;
    }
    starterCommon.hideSoftInputMethod();
  }

  public void showSoftInputMethod() {
    if (starterCommon == null) {
      return;
    }
    starterCommon.showSoftInputMethod();
  }

  public boolean isImmActive() {
    return starterCommon != null && starterCommon.isImmActive();
  }
}
