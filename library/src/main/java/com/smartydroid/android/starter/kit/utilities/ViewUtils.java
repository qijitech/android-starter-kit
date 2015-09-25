/**
 * Created by YuGang Yang on September 23, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.utilities;

import android.app.Activity;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Utilities for working with the {@link View} class
 */
public class ViewUtils {

  /**
   * Set visibility of given view to be gone or visible
   * <p>
   * This method has no effect if the view visibility is currently invisible
   *
   * @param view
   * @param gone
   * @return view
   */
  public static <V extends View> V setGone(final V view, final boolean gone) {
    if (view != null)
      if (gone) {
        if (GONE != view.getVisibility())
          view.setVisibility(GONE);
      } else {
        if (VISIBLE != view.getVisibility())
          view.setVisibility(VISIBLE);
      }
    return view;
  }

  /**
   * Set visibility of given view to be invisible or visible
   * <p>
   * This method has no effect if the view visibility is currently gone
   *
   * @param view
   * @param invisible
   * @return view
   */
  public static <V extends View> V setInvisible(final V view,
      final boolean invisible) {
    if (view != null)
      if (invisible) {
        if (INVISIBLE != view.getVisibility())
          view.setVisibility(INVISIBLE);
      } else {
        if (VISIBLE != view.getVisibility())
          view.setVisibility(VISIBLE);
      }
    return view;
  }

  /**
   * Generics version of {@link android.app.Activity#findViewById}
   * @param parent
   * @param viewId
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked") public static <T extends View> T getViewOrNull(Activity parent,
      int viewId) {
    return (T) parent.findViewById(viewId);
  }

  /** Generics version of {@link android.view.View#findViewById} */
  @SuppressWarnings("unchecked") public static <T extends View> T getViewOrNull(View parent,
      int viewId) {
    return (T) parent.findViewById(viewId);
  }

  /**
   * Same as {@link android.app.Activity#findViewById}, but crashes if there's no view.
   * @param parent
   * @param viewId
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked") public static <T extends View> T getView(Activity parent,
      int viewId) {
    return (T) checkView(parent.findViewById(viewId));
  }

  /**
   * Same as {@link android.view.View#findViewById}, but crashes if there's no view.
   * @param parent
   * @param viewId
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked") public static <T extends View> T getView(View parent, int viewId) {
    return (T) checkView(parent.findViewById(viewId));
  }

  private static View checkView(View v) {
    if (v == null) {
      throw new IllegalArgumentException("View doesn't exist");
    }
    return v;
  }

  private ViewUtils() {

  }
}
