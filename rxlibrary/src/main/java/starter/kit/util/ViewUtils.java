package starter.kit.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

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
   * @param view View
   * @param gone boolean
   * @return view View
   */
  public static <V extends View> V setGone(final V view, final boolean gone) {
    if (view != null) {
      if (gone) {
        if (GONE != view.getVisibility()) view.setVisibility(GONE);
      } else {
        if (VISIBLE != view.getVisibility()) view.setVisibility(VISIBLE);
      }
    }
    return view;
  }

  /**
   * Set visibility of given view to be invisible or visible
   * <p>
   * This method has no effect if the view visibility is currently gone
   *
   * @param view View
   * @param invisible boolean
   * @return view View
   */
  public static <V extends View> V setInvisible(final V view, final boolean invisible) {
    if (view != null) {
      if (invisible) {
        if (INVISIBLE != view.getVisibility()) view.setVisibility(INVISIBLE);
      } else {
        if (VISIBLE != view.getVisibility()) view.setVisibility(VISIBLE);
      }
    }
    return view;
  }

  /**
   * Generics version of {@link Activity#findViewById}
   *
   * @param parent Activity
   * @param viewId viewId
   * @param <T> extends View
   * @return View
   */
  @SuppressWarnings("unchecked") public static <T extends View> T getViewOrNull(Activity parent,
      int viewId) {
    return (T) parent.findViewById(viewId);
  }

  /** Generics version of {@link View#findViewById} */
  @SuppressWarnings("unchecked") public static <T extends View> T getViewOrNull(View parent,
      int viewId) {
    return (T) parent.findViewById(viewId);
  }

  /**
   * Same as {@link Activity#findViewById}, but crashes if there's no view.
   *
   * @param parent Activity
   * @param viewId viewId
   * @param <T> View
   * @return View
   */
  @SuppressWarnings("unchecked") public static <T extends View> T getView(Activity parent,
      int viewId) {
    return (T) checkView(parent.findViewById(viewId));
  }

  /**
   * Same as {@link View#findViewById}, but crashes if there's no view.
   *
   * @param parent View
   * @param viewId viewId
   * @param <T> View
   * @return View
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

  @SuppressWarnings("deprecation")
  public static void removeOnGlobalLayoutListener(View view,
      ViewTreeObserver.OnGlobalLayoutListener listener) {
    if (Build.VERSION.SDK_INT < 16) {
      view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
    } else {
      view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
  }

  private ViewUtils() {

  }
}
