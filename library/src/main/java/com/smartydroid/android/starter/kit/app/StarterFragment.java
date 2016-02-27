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
    starterCommon = StarterCommon.create(getActivity());
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

  public void showHud() {
    showHud(null);
  }

  public void showHud(int resId) {
    showHud(getString(resId));
  }

  public void showHud(String text) {
    showHud(text, true);
  }

  public void showHud(String text, boolean isCancellable) {
    if (starterCommon != null) {
      starterCommon.showHud(text, isCancellable);
    }
  }

  public void dismissHud() {
    if (starterCommon != null) {
      starterCommon.dismissHud();
    }
  }

  public void hideSoftInputMethod() {
    if (starterCommon != null) {
      starterCommon.hideSoftInputMethod();
    }
  }

  public void showSoftInputMethod() {
    if (starterCommon != null) {
      starterCommon.showSoftInputMethod();
    }
  }

  public boolean isImmActive() {
    return starterCommon != null && starterCommon.isImmActive();
  }
}
