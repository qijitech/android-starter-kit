/**
 * Created by YuGang Yang on October 31, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.smartydroid.android.starter.kit.R;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

public class ProgressLoading extends Dialog {

  private ProgressBar mProgressBar;
  private TextView mTextView;

  private CharSequence mText;
  private boolean mShowProgress = true;

  public ProgressLoading(Context context, int theme) {
    super(context, theme);
    initialize();
  }

  private void initialize() {
    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.widget_progress);
    mTextView = (TextView) findViewById(android.R.id.text1);
    mProgressBar = (ProgressBar) findViewById(android.R.id.progress);
  }

  public ProgressLoading text(int resId) {
    return text(getContext().getString(resId));
  }

  public ProgressLoading text(CharSequence text) {
    mText = text;
    return this;
  }

  public ProgressLoading updateText(CharSequence text) {
    mText = text;
    show();
    return this;
  }

  public ProgressLoading hideText() {
    return updateText("");
  }

  public ProgressLoading hideProgressBar() {
    mShowProgress = false;
    show();
    return this;
  }

  @Override public void show() {
    if (TextUtils.isEmpty(mText)) {
      ViewUtils.setGone(mTextView, true);
    } else {
      ViewUtils.setGone(mTextView, false);
      mTextView.setText(mText);
    }

    if (mShowProgress) {
      ViewUtils.setGone(mProgressBar, false);
    } else {
      ViewUtils.setGone(mProgressBar, true);
    }

    super.show();
  }
}
