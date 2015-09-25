/**
 * Created by YuGang Yang on September 23, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class LoadingLayout extends ViewSwitcher implements View.OnClickListener {

  private static final String TAG_LOADING = "LoadingLayout.TAG_LOADING";
  private static final String TAG_EMPTY = "LoadingLayout.TAG_EMPTY";
  private static final String TAG_ERROR = "LoadingLayout.TAG_ERROR";

  private static final int DEFAULT_IMAGE_WIDTH = 308;
  private static final int DEFAULT_IMAGE_HEIGHT = 308;
  private static final int DEFAULT_TITLE_TEXT_SIZE = 16;
  private static final int DEFAULT_SUBTITLE_TEXT_SIZE = 14;

  public static final int VIEW_STATE_CONTENT = 0;
  public static final int VIEW_STATE_ERROR = 1;
  public static final int VIEW_STATE_EMPTY = 2;
  public static final int VIEW_STATE_LOADING = 3;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ VIEW_STATE_CONTENT, VIEW_STATE_ERROR, VIEW_STATE_EMPTY, VIEW_STATE_LOADING })
  public @interface ViewState {
  }

  LayoutInflater mLayoutInflater;

  @ViewState private int mDefaultViewState = VIEW_STATE_CONTENT;
  @ViewState private int mCurrentViewStatus = mDefaultViewState;

  FrameLayout mContainer;
  View mContentView;

  View mEmptyView;
  ImageView mEmptyImageView;
  TextView mEmptyTitleTextView;
  TextView mEmptyContentTextView;

  View mErrorView;
  ImageView mErrorImageView;
  TextView mErrorTitleTextView;
  TextView mErrorContentTextView;

  Button mErrorButton;
  Button mEmptyButton;

  View mLoadingView;
  ProgressBar mLoadingProgressBar;

  int mEmptyLayoutRes;
  int mErrorLayoutRes;
  int mLoadingLayoutRes;
  int mContentLayoutRes;

  int mLoadingProgressBarWidth;
  int mLoadingProgressBarHeight;
  int mLoadingBackgroundColor;

  int mEmptyImageWidth;
  int mEmptyImageHeight;
  int mEmptyTitleTextSize;
  int mEmptyContentTextSize;
  int mEmptyTitleTextColor;
  int mEmptyContentTextColor;
  int mEmptyBackgroundColor;

  int mErrorImageWidth;
  int mErrorImageHeight;
  int mErrorTitleTextSize;
  int mErrorContentTextSize;
  int mErrorTitleTextColor;
  int mErrorContentTextColor;
  int mErrorBackgroundColor;

  int mButtonTextColor;
  int mButtonBackgroundRes;

  private WeakReference<OnButtonClickListener> mListenerRef;

  public LoadingLayout(Context context) {
    this(context, null);
  }

  public LoadingLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs);
  }

  /**
   * 初始化
   */
  private void initialize(Context context, AttributeSet attrs) {

    mLayoutInflater = LayoutInflater.from(context);
    // get system attrs (android:textSize and android:textColor)

    TypedArray a = null;

    try {
      a = getContext().obtainStyledAttributes(attrs, R.styleable.StarterLoadingLayout);

      mEmptyLayoutRes = a.getResourceId(R.styleable.StarterLoadingLayout_emptyLayoutRes, R.layout.include_empty_view);
      mErrorLayoutRes = a.getResourceId(R.styleable.StarterLoadingLayout_errorLayoutRes, R.layout.include_error_view);
      mLoadingLayoutRes = a.getResourceId(R.styleable.StarterLoadingLayout_loadingLayoutRes, R.layout.include_loading_view);
      mContentLayoutRes = a.getResourceId(R.styleable.StarterLoadingLayout_contentLayoutRes, R.layout.include_recycler_view);

      // init children view
      mContentView = mLayoutInflater.inflate(mContentLayoutRes, this, false);
      mContentView.setId(R.id.container_starter_content);
      addView(mContentView, 0);
      mContainer = new FrameLayout(context);
      mContainer.setId(R.id.container_starter_loading_empty_error);
      addView(mContainer, 1);

      //Loading state attrs
      mLoadingProgressBarWidth = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_loadingProgressBarWidth, 0);
      mLoadingProgressBarHeight = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_loadingProgressBarHeight, 0);
      mLoadingBackgroundColor = a.getColor(R.styleable.StarterLoadingLayout_loadingBackgroundColor, Color.TRANSPARENT);

      //Empty state attrs
      mEmptyImageWidth = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_emptyImageWidth, DEFAULT_IMAGE_WIDTH);
      mEmptyImageHeight = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_emptyImageHeight, DEFAULT_IMAGE_HEIGHT);
      mEmptyTitleTextSize = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_emptyTitleTextSize, DEFAULT_TITLE_TEXT_SIZE);
      mEmptyContentTextSize = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_emptyContentTextSize, DEFAULT_SUBTITLE_TEXT_SIZE);
      mEmptyTitleTextColor = a.getColor(R.styleable.StarterLoadingLayout_emptyTitleTextColor, Color.BLACK);
      mEmptyContentTextColor = a.getColor(R.styleable.StarterLoadingLayout_emptyContentTextColor, Color.BLACK);
      mEmptyBackgroundColor = a.getColor(R.styleable.StarterLoadingLayout_emptyBackgroundColor, Color.TRANSPARENT);

      //Error state attrs
      mErrorImageWidth = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_errorImageWidth, DEFAULT_IMAGE_WIDTH);
      mErrorImageHeight = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_errorImageHeight, DEFAULT_IMAGE_HEIGHT);
      mErrorTitleTextSize = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_errorTitleTextSize, DEFAULT_TITLE_TEXT_SIZE);
      mErrorContentTextSize = a.getDimensionPixelSize(R.styleable.StarterLoadingLayout_errorContentTextSize, DEFAULT_SUBTITLE_TEXT_SIZE);
      mErrorTitleTextColor = a.getColor(R.styleable.StarterLoadingLayout_errorTitleTextColor, Color.BLACK);
      mErrorContentTextColor = a.getColor(R.styleable.StarterLoadingLayout_errorContentTextColor, Color.BLACK);
      mErrorBackgroundColor = a.getColor(R.styleable.StarterLoadingLayout_errorBackgroundColor, Color.TRANSPARENT);

      mButtonTextColor = a.getColor(R.styleable.StarterLoadingLayout_buttonTextColor, Color.BLACK);
      mButtonBackgroundRes = a.getResourceId(R.styleable.StarterLoadingLayout_buttonBackground, 0);

      final int viewState = a.getInt(R.styleable.StarterLoadingLayout_viewState, VIEW_STATE_CONTENT);
      switch (viewState) {
        case VIEW_STATE_CONTENT:
          mDefaultViewState = VIEW_STATE_CONTENT;
          break;
        case VIEW_STATE_ERROR:
          mDefaultViewState = VIEW_STATE_ERROR;
          break;
        case VIEW_STATE_EMPTY:
          mDefaultViewState = VIEW_STATE_EMPTY;
          break;
        case VIEW_STATE_LOADING:
          mDefaultViewState = VIEW_STATE_LOADING;
          break;
      }

      if (mDefaultViewState != mCurrentViewStatus) {
        switchState(mDefaultViewState);
      }
    } finally {
      if (a != null) {
        a.recycle();
      }
    }
  }

  /**
   * 设置按钮监听时间
   * @param listener
   */
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    mListenerRef = new WeakReference<>(listener);
  }

  /**
   *
   * @return
   */
  private OnButtonClickListener getOnButtonClickListener() {
    return mListenerRef == null ? null : mListenerRef.get();
  }

  /**
   * display content view
   */
  public void showContentView() {
    switchState(VIEW_STATE_CONTENT, null, null, null, null, null);
  }

  /**
   * display loading view
   */
  public void showLoadingView() {
    switchState(VIEW_STATE_LOADING, null, null, null, null, null);
  }

  /**
   * display empty view
   */
  public void showEmptyView() {
    switchState(VIEW_STATE_EMPTY, null, null, null, null, null);
  }

  /**
   * dispaly empty view with click listener
   * @param onClickListener
   */
  public void showEmptyView(OnClickListener onClickListener) {
    switchState(VIEW_STATE_EMPTY, null, null, null, null, onClickListener);
  }

  /**
   * dispaly empty view
   * @param emptyImageDrawable
   * @param emptyTextTitle
   * @param emptyTextContent
   * @param emptyButtonText
   * @param onClickListener
   */
  public void showEmptyView(Drawable emptyImageDrawable, String emptyTextTitle, String emptyTextContent,
      String emptyButtonText, OnClickListener onClickListener) {
    switchState(VIEW_STATE_EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent,
        emptyButtonText, onClickListener);
  }

  /**
   * dispaly empty view
   * @param emptyImageDrawable
   * @param emptyTextTitle
   * @param emptyTextContent
   * @param emptyButtonText
   */
  public void showEmptyView(Drawable emptyImageDrawable, String emptyTextTitle, String emptyTextContent,
      String emptyButtonText) {
    switchState(VIEW_STATE_EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent,
        emptyButtonText, null);
  }

  /**
   * display empty view
   * @param emptyImageDrawableRes
   * @param emptyTextTitleRes
   * @param emptyTextContentRes
   * @param emptyButtonTextRes
   * @param onClickListener
   */
  public void showEmptyView(int emptyImageDrawableRes, int emptyTextTitleRes,
      int emptyTextContentRes, int emptyButtonTextRes, OnClickListener onClickListener) {
    switchState(VIEW_STATE_EMPTY, getDrawable(emptyImageDrawableRes), getString(emptyTextTitleRes),
        getString(emptyTextContentRes), getString(emptyButtonTextRes), onClickListener);
  }

  /**
   * display empty view
   * @param emptyImageDrawableRes
   * @param emptyTextTitleRes
   * @param emptyTextContentRes
   * @param onClickListener
   */
  public void showEmptyView(int emptyImageDrawableRes, int emptyTextTitleRes,
      int emptyTextContentRes, OnClickListener onClickListener) {
    switchState(VIEW_STATE_EMPTY, getDrawable(emptyImageDrawableRes), getString(emptyTextTitleRes),
        getString(emptyTextContentRes), null, onClickListener);
  }

  /**
   * display empty view
   * @param emptyImageDrawableRes
   * @param emptyTextTitleRes
   * @param emptyTextContentRes
   */
  public void showEmptyView(int emptyImageDrawableRes, int emptyTextTitleRes,
      int emptyTextContentRes) {
    switchState(VIEW_STATE_EMPTY, getDrawable(emptyImageDrawableRes), getString(emptyTextTitleRes),
        getString(emptyTextContentRes), null, null);
  }

  public void showErrorView() {
    switchState(VIEW_STATE_ERROR, null, null, null, null, null);
  }

  /**
   * dispaly error view with click listener
   * @param onClickListener
   */
  public void showErrorView(OnClickListener onClickListener) {
    switchState(VIEW_STATE_ERROR, null, null, null, null, onClickListener);
  }

  /**
   * dispaly error view
   * @param errorImageDrawable
   * @param errorTextTitle
   * @param errorTextContent
   * @param errorButtonText
   * @param onClickListener
   */
  public void showErrorView(Drawable errorImageDrawable, String errorTextTitle, String errorTextContent,
      String errorButtonText, OnClickListener onClickListener) {
    switchState(VIEW_STATE_ERROR, errorImageDrawable, errorTextTitle, errorTextContent,
        errorButtonText, onClickListener);
  }

  /**
   * dispaly error view
   * @param errorImageDrawable
   * @param errorTextTitle
   * @param errorTextContent
   * @param errorButtonText
   */
  public void showErrorView(Drawable errorImageDrawable, String errorTextTitle, String errorTextContent,
      String errorButtonText) {
    switchState(VIEW_STATE_ERROR, errorImageDrawable, errorTextTitle, errorTextContent,
        errorButtonText, null);
  }

  /**
   * display error view
   * @param errorImageDrawableRes
   * @param errorTextTitleRes
   * @param errorTextContentRes
   * @param errorButtonTextRes
   * @param onClickListener
   */
  public void showErrorView(int errorImageDrawableRes, int errorTextTitleRes,
      int errorTextContentRes, int errorButtonTextRes, OnClickListener onClickListener) {
    switchState(VIEW_STATE_ERROR, getDrawable(errorImageDrawableRes), getString(errorTextTitleRes),
        getString(errorTextContentRes), getString(errorButtonTextRes), onClickListener);
  }

  /**
   * display error view
   * @param errorImageDrawableRes
   * @param errorTextTitleRes
   * @param errorTextContentRes
   * @param onClickListener
   */
  public void showErrorView(int errorImageDrawableRes, int errorTextTitleRes,
      int errorTextContentRes, OnClickListener onClickListener) {
    switchState(VIEW_STATE_ERROR, getDrawable(errorImageDrawableRes), getString(errorTextTitleRes),
        getString(errorTextContentRes), null, onClickListener);
  }

  /**
   * display error view
   * @param errorImageDrawableRes
   * @param errorTextTitleRes
   * @param errorTextContentRes
   */
  public void showErrorView(int errorImageDrawableRes, int errorTextTitleRes,
      int errorTextContentRes) {
    switchState(VIEW_STATE_ERROR, getDrawable(errorImageDrawableRes), getString(errorTextTitleRes),
        getString(errorTextContentRes), null, null);
  }

  /**
   * get content view
   * @return
   */
  public <T> T getContentView() {
    return (T) mContentView;
  }

  /**
   * Check if content is shown
   *
   * @return boolean
   */
  public boolean isContentShown() {
    return mCurrentViewStatus == VIEW_STATE_CONTENT;
  }

  /**
   * Check if loading state is shown
   *
   * @return boolean
   */
  public boolean isLoadingShown() {
    return mCurrentViewStatus == VIEW_STATE_LOADING;
  }

  /**
   * Check if empty state is shown
   *
   * @return boolean
   */
  public boolean isEmptyShown() {
    return mCurrentViewStatus == VIEW_STATE_EMPTY;
  }

  /**
   * Check if error state is shown
   *
   * @return boolean
   */
  public boolean isError() {
    return mCurrentViewStatus == VIEW_STATE_ERROR;
  }

  private void switchState(int state) {
    switchState(state, null, null, null, null, null);
  }

  private void switchState(int state, Drawable drawable, String errorText, String errorTextContent,
      String errorButtonText, OnClickListener onClickListener) {
    if (mCurrentViewStatus == state) return;
    mCurrentViewStatus = state;

    switch (state) {
      case VIEW_STATE_CONTENT:
        hide(mLoadingView).hide(mEmptyView).hide(mErrorView);
        dispalyContentView();
        break;
      case VIEW_STATE_EMPTY:
        hide(mLoadingView).hide(mErrorView);
        setupEmptyView(drawable, errorText, errorTextContent, errorButtonText, onClickListener);
        dispalyPlaceHolderView();
        break;
      case VIEW_STATE_LOADING:
        hide(mEmptyView).hide(mErrorView);
        setupLoadingView();
        dispalyPlaceHolderView();
        break;
      case VIEW_STATE_ERROR:
        hide(mLoadingView).hide(mEmptyView);
        setupErrorView(drawable, errorText, errorTextContent, errorButtonText, onClickListener);
        dispalyPlaceHolderView();
        break;
    }
  }

  private LoadingLayout fadeIn(final View view, final boolean animate) {
    if (view != null) {
      if (animate && getInAnimation() != null) {
        view.startAnimation(getInAnimation());
      } else {
        view.clearAnimation();
      }
    }
    return this;
  }

  private LoadingLayout show(final View view) {
    ViewUtils.setGone(view, false);
    return this;
  }

  private LoadingLayout hide(final View view) {
    ViewUtils.setGone(view, true);
    return this;
  }

  private boolean isNull(String str) {
    return str == null;
  }

  /**
   * @return String
   */
  private String getString(int res) {
    return getContext().getApplicationContext().getString(res);
  }

  /**
   * @return drawable
   */
  private Drawable getDrawable(int res) {
    return ResourcesCompat.getDrawable(getResources(), res, getContext().getTheme());
  }

  /**
   * display load view, empty view error view
   */
  private void dispalyPlaceHolderView() {
    final int currentViewId = getCurrentView().getId();
    if (currentViewId != R.id.container_starter_loading_empty_error
        || currentViewId == R.id.container_content) {
      showNext();
      return;
    }
  }

  /**
   * display content view
   */
  private void dispalyContentView() {
    final int currentViewId = getCurrentView().getId();
    if (currentViewId == R.id.container_starter_loading_empty_error
        || currentViewId != R.id.container_content) {
      showPrevious();
      return;
    }
  }

  /**
   * 设置错误页面
   */
  private void setupErrorView(Drawable drawable, String errorText, String errorTextContent,
      String errorButtonText, OnClickListener onClickListener) {
    if (mErrorView == null) {
      final View errorView = mLayoutInflater.inflate(R.layout.include_error_view, null);
      errorView.setTag(TAG_ERROR);

      mErrorImageView = (ImageView) errorView.findViewById(android.R.id.icon);
      mErrorTitleTextView = (TextView) errorView.findViewById(android.R.id.text1);
      mErrorContentTextView = (TextView) errorView.findViewById(android.R.id.text2);
      mErrorButton = (Button) errorView.findViewById(android.R.id.button1);

      //Set error state image width and height
      mErrorImageView.getLayoutParams().width = mErrorImageWidth;
      mErrorImageView.getLayoutParams().height = mErrorImageHeight;
      mErrorImageView.requestLayout();

      mErrorTitleTextView.setTextSize(mErrorTitleTextSize);
      mErrorContentTextView.setTextSize(mErrorContentTextSize);
      mErrorTitleTextView.setTextColor(mErrorTitleTextColor);
      mErrorContentTextView.setTextColor(mErrorContentTextColor);
      mErrorButton.setTextColor(mButtonTextColor);
      if (mButtonBackgroundRes > 0) {
        mErrorButton.setBackgroundResource(mButtonBackgroundRes);
      }

      if (drawable != null) {
        mErrorImageView.setImageDrawable(drawable);
      }

      if (!isNull(errorText)) {
        mErrorTitleTextView.setText(errorText);
      }

      if (!isNull(errorTextContent)) {
        mErrorContentTextView.setText(errorTextContent);
      }

      if (!isNull(errorButtonText)) {
        mErrorButton.setText(errorButtonText);
      }

      if (onClickListener != null) {
        ViewUtils.setGone(mErrorButton, false);
        mErrorButton.setOnClickListener(onClickListener);
      } else if (getOnButtonClickListener() != null) {
        ViewUtils.setGone(mErrorButton, false);
        mErrorButton.setOnClickListener(this);
      } else {
        ViewUtils.setGone(mErrorButton, true);
      }

      //Set background color if not TRANSPARENT
      if (mErrorBackgroundColor != Color.TRANSPARENT) {
        errorView.setBackgroundColor(mErrorBackgroundColor);
      }

      mContainer.addView(errorView);
      mErrorView = errorView;
    } else {
      fadeIn(mErrorView, true).show(mErrorView);
    }
  }

  /**
   * 设置空白页面
   */
  private void setupEmptyView(Drawable drawable, String errorText, String errorTextContent,
      String errorButtonText, OnClickListener onClickListener) {
    if (mEmptyView == null) {
      final View emptyView = mLayoutInflater.inflate(R.layout.include_empty_view, null);
      emptyView.setTag(TAG_EMPTY);

      mEmptyImageView = (ImageView) emptyView.findViewById(android.R.id.icon);
      mEmptyTitleTextView = (TextView) emptyView.findViewById(android.R.id.text1);
      mEmptyContentTextView = (TextView) emptyView.findViewById(android.R.id.text2);
      mEmptyButton = (Button) emptyView.findViewById(android.R.id.button1);

      //Set empty state image width and height
      mEmptyImageView.getLayoutParams().width = mEmptyImageWidth;
      mEmptyImageView.getLayoutParams().height = mEmptyImageHeight;
      mEmptyImageView.requestLayout();

      mEmptyTitleTextView.setTextSize(mEmptyTitleTextSize);
      mEmptyContentTextView.setTextSize(mEmptyContentTextSize);
      mEmptyTitleTextView.setTextColor(mEmptyTitleTextColor);
      mEmptyContentTextView.setTextColor(mEmptyContentTextColor);

      //Set background color if not TRANSPARENT
      if (mEmptyBackgroundColor != Color.TRANSPARENT) {
        emptyView.setBackgroundColor(mEmptyBackgroundColor);
      }

      if (drawable != null) {
        mEmptyImageView.setImageDrawable(drawable);
      }

      if (!isNull(errorText)) {
        mEmptyTitleTextView.setText(errorText);
      }

      if (!isNull(errorText)) {
        mEmptyContentTextView.setText(errorTextContent);
      }

      if (!isNull(errorButtonText)) {
        mEmptyButton.setText(errorButtonText);
      }

      if (onClickListener != null) {
        ViewUtils.setGone(mEmptyButton, false);
        mEmptyButton.setOnClickListener(onClickListener);
      } else if (getOnButtonClickListener() != null) {
        ViewUtils.setGone(mEmptyButton, false);
        mEmptyButton.setOnClickListener(this);
      } else {
        ViewUtils.setGone(mEmptyButton, true);
      }

      mContainer.addView(emptyView);
      mEmptyView = emptyView;
    } else {
      fadeIn(mEmptyView, true).show(mEmptyView);
    }
  }

  /**
   * 设置加载页面
   */
  private void setupLoadingView() {
    if (mLoadingView == null) {
      final View loadingView = mLayoutInflater.inflate(R.layout.include_loading_view, null);
      loadingView.setTag(TAG_LOADING);

      mLoadingProgressBar = (ProgressBar) loadingView.findViewById(android.R.id.progress);

      if (mLoadingProgressBarWidth > 0 && mLoadingProgressBarHeight > 0) {
        mLoadingProgressBar.getLayoutParams().width = mLoadingProgressBarWidth;
        mLoadingProgressBar.getLayoutParams().height = mLoadingProgressBarHeight;
        mLoadingProgressBar.requestLayout();
      }

      //Set background color if not TRANSPARENT
      if (mLoadingBackgroundColor != Color.TRANSPARENT) {
        mLoadingProgressBar.setBackgroundColor(mLoadingBackgroundColor);
      }

      mContainer.addView(loadingView);
      mLoadingView = loadingView;
    } else {
      fadeIn(mLoadingView, true).show(mLoadingView);
    }
  }

  @Override public void onClick(View v) {
    if (getOnButtonClickListener() != null) {
      if (v == mEmptyButton) {
        getOnButtonClickListener().onEmptyButtonClick(v);
        return;
      }

      if (v == mErrorButton) {
        getOnButtonClickListener().onErrorButtonClick(v);
      }
    }
  }

  /**
   * button click listener
   */
  public interface OnButtonClickListener {
    void onEmptyButtonClick(View view);
    void onErrorButtonClick(View view);
  }

}
