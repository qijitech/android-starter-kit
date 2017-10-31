/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package support.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import java.util.HashMap;
import java.util.Map;

/**
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and
 * progress animation, call setEnabled(false) on the view.
 * <p>
 * This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.
 * </p>
 */
public class SwipeRefreshLayout extends ViewGroup
    implements NestedScrollingParent, NestedScrollingChild {
  // Maps to ProgressBar.Large style
  public static final int LARGE = MaterialProgressDrawable.LARGE;
  // Maps to ProgressBar default style
  public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;

  private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();

  private static final int MAX_ALPHA = 255;
  private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

  private static final int CIRCLE_DIAMETER = 40;
  private static final int CIRCLE_DIAMETER_LARGE = 56;

  private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
  private static final int INVALID_POINTER = -1;
  private static final float DRAG_RATE = .5f;

  // Max amount of circle that can be filled by progress during swipe gesture,
  // where 1.0 is a full circle
  private static final float MAX_PROGRESS_ANGLE = .8f;

  private static final int SCALE_DOWN_DURATION = 150;

  private static final int ALPHA_ANIMATION_DURATION = 300;

  private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

  private static final int ANIMATE_TO_START_DURATION = 200;

  // Default background for the progress spinner
  private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
  // Default offset in dips from the top of the view to where the progress spinner should stop
  private static final int DEFAULT_CIRCLE_TARGET = 64;
  private static final int[] LAYOUT_ATTRS = new int[] {
      android.R.attr.enabled
  };
  private final NestedScrollingParentHelper mNestedScrollingParentHelper;
  private final NestedScrollingChildHelper mNestedScrollingChildHelper;
  private final int[] mParentScrollConsumed = new int[2];
  private final int[] mParentOffsetInWindow = new int[2];
  private final DecelerateInterpolator mDecelerateInterpolator;
  protected int mFrom;
  protected int mOriginalOffsetTop;
  private View mTarget; // the target of the gesture
  private OnRefreshListener mListener;
  private boolean mRefreshing = false;
  private int mTouchSlop;
  private float mTotalDragDistance = -1;
  // If nested scrolling is enabled, the total amount that needed to be
  // consumed by this as the nested scrolling parent is used in place of the
  // overscroll determined by MOVE events in the onTouch handler
  private float mTotalUnconsumed;
  private boolean mNestedScrollInProgress;
  private int mMediumAnimationDuration;
  private int mCurrentTargetOffsetTop;
  // Whether or not the starting offset has been determined.
  private boolean mOriginalOffsetCalculated = false;
  private float mInitialMotionY;
  private float mInitialDownY;
  private boolean mIsBeingDragged;
  private int mActivePointerId = INVALID_POINTER;
  // Whether this item is scaled up rather than clipped
  private boolean mScale;
  // Target is returning to its start offset because it was cancelled or a
  // refresh was triggered.
  private boolean mReturningToStart;
  private CircleImageView mCircleView;
  private final Animation mAnimateToStartPosition = new Animation() {
    @Override public void applyTransformation(float interpolatedTime, Transformation t) {
      moveToStart(interpolatedTime);
    }
  };
  private int mCircleViewIndex = -1;
  private float mStartingScale;
  private MaterialProgressDrawable mProgress;
  private Animation mScaleAnimation;
  private Animation mScaleDownAnimation;
  private Animation mAlphaStartAnimation;
  private Animation mAlphaMaxAnimation;
  private Animation mScaleDownToStartAnimation;
  private float mSpinnerFinalOffset;
  private boolean mNotify;
  private int mCircleWidth;
  private int mCircleHeight;
  // Whether the client has set a custom starting position;
  private boolean mUsingCustomStart;
  private Direction mDirection;
  private final Animation mAnimateToCorrectPosition = new Animation() {
    @Override public void applyTransformation(float interpolatedTime, Transformation t) {
      int targetTop;
      int endTarget;
      if (!mUsingCustomStart) {
        switch (mDirection) {
          case BOTTOM:
            endTarget = getMeasuredHeight() - (int) (mSpinnerFinalOffset);
            break;
          case TOP:
          default:
            endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            break;
        }
      } else {
        endTarget = (int) mSpinnerFinalOffset;
      }
      targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
      int offset = targetTop - mCircleView.getTop();
      setTargetOffsetTopAndBottom(offset, false /* requires update */);
      mProgress.setArrowScale(1 - interpolatedTime);
    }
  };
  private boolean mBothDirection;
  private AnimationListener mRefreshListener = new AnimationListener() {
    @Override public void onAnimationStart(Animation animation) {
    }

    @Override public void onAnimationRepeat(Animation animation) {
    }

    @Override public void onAnimationEnd(Animation animation) {
      if (mRefreshing) {
        // Make sure the progress view is fully visible
        mProgress.setAlpha(MAX_ALPHA);
        mProgress.start();
        if (mNotify) {
          if (mListener != null) {
            mListener.onRefresh(mDirection);
          }
        }
        mCurrentTargetOffsetTop = mCircleView.getTop();
      } else {
        reset();
      }
    }
  };

  /**
   * Simple constructor to use when creating a SwipeRefreshLayout from code.
   */
  public SwipeRefreshLayout(Context context) {
    this(context, null);
  }

  /**
   * Constructor that is called when inflating SwipeRefreshLayout from XML.
   */
  public SwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

    setWillNotDraw(false);
    mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
    setEnabled(a.getBoolean(0, true));
    a.recycle();

    final TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.SwipeRefreshLayout);
    Direction direction =
        Direction.fromValue(a2.getInt(R.styleable.SwipeRefreshLayout_srl_direction, 0));
    if (direction != Direction.BOTH) {
      mDirection = direction;
      mBothDirection = false;
    } else {
      mDirection = Direction.TOP;
      mBothDirection = true;
    }
    a2.recycle();

    final DisplayMetrics metrics = getResources().getDisplayMetrics();
    mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
    mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);

    createProgressView();
    ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    // the absolute offset has to take into account that the circle starts at an offset
    mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
    mTotalDragDistance = mSpinnerFinalOffset;
    mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);
  }

  private void reset() {
    mCircleView.clearAnimation();
    mProgress.stop();
    mCircleView.setVisibility(View.GONE);
    setColorViewAlpha(MAX_ALPHA);
    // Return the circle to its start position
    if (mScale) {
      setAnimationProgress(0 /* animation complete and view is hidden */);
    } else {
      setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true /* requires update */);
    }
    mCurrentTargetOffsetTop = mCircleView.getTop();
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    reset();
  }

  private void setColorViewAlpha(int targetAlpha) {
    mCircleView.getBackground().setAlpha(targetAlpha);
    mProgress.setAlpha(targetAlpha);
  }

  /**
   * The refresh indicator starting and resting position is always positioned
   * near the top of the refreshing content. This position is a consistent
   * location, but can be adjusted in either direction based on whether or not
   * there is a toolbar or actionbar present.
   *
   * @param scale Set to true if there is no view at a higher z-order than
   * where the progress spinner is set to appear.
   * @param start The offset in pixels from the top of this view at which the
   * progress spinner should appear.
   * @param end The offset in pixels from the top of this view at which the
   * progress spinner should come to rest after a successful swipe
   * gesture.
   */
  public void setProgressViewOffset(boolean scale, int start, int end) {
    mScale = scale;
    mCircleView.setVisibility(View.GONE);
    mOriginalOffsetTop = mCurrentTargetOffsetTop = start;
    mSpinnerFinalOffset = end;
    mUsingCustomStart = true;
    mCircleView.invalidate();
  }

  /**
   * The refresh indicator resting position is always positioned near the top
   * of the refreshing content. This position is a consistent location, but
   * can be adjusted in either direction based on whether or not there is a
   * toolbar or actionbar present.
   *
   * @param scale Set to true if there is no view at a higher z-order than
   * where the progress spinner is set to appear.
   * @param end The offset in pixels from the top of this view at which the
   * progress spinner should come to rest after a successful swipe
   * gesture.
   */
  public void setProgressViewEndTarget(boolean scale, int end) {
    mSpinnerFinalOffset = end;
    mScale = scale;
    mCircleView.invalidate();
  }

  /**
   * One of DEFAULT, or LARGE.
   */
  public void setSize(int size) {
    if (size != MaterialProgressDrawable.LARGE && size != MaterialProgressDrawable.DEFAULT) {
      return;
    }
    final DisplayMetrics metrics = getResources().getDisplayMetrics();
    if (size == MaterialProgressDrawable.LARGE) {
      mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
    } else {
      mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
    }
    // force the bounds of the progress circle inside the circle view to
    // update by setting it to null before updating its size and then
    // re-setting it
    mCircleView.setImageDrawable(null);
    mProgress.updateSizes(size);
    mCircleView.setImageDrawable(mProgress);
  }

  protected int getChildDrawingOrder(int childCount, int i) {
    if (mCircleViewIndex < 0) {
      return i;
    } else if (i == childCount - 1) {
      // Draw the selected child last
      return mCircleViewIndex;
    } else if (i >= mCircleViewIndex) {
      // Move the children after the selected child earlier one
      return i + 1;
    } else {
      // Keep the children before the selected child the same
      return i;
    }
  }

  private void createProgressView() {
    mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2);
    mProgress = new MaterialProgressDrawable(getContext(), this);
    mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
    mCircleView.setImageDrawable(mProgress);
    mCircleView.setVisibility(View.GONE);
    addView(mCircleView);
  }

  /**
   * Set the listener to be notified when a refresh is triggered via the swipe
   * gesture.
   */
  public void setOnRefreshListener(OnRefreshListener listener) {
    mListener = listener;
  }

  /**
   * Pre API 11, alpha is used to make the progress circle appear instead of scale.
   */
  private boolean isAlphaUsedForScale() {
    return android.os.Build.VERSION.SDK_INT < 11;
  }

  private void startScaleUpAnimation(AnimationListener listener) {
    mCircleView.setVisibility(View.VISIBLE);
    if (android.os.Build.VERSION.SDK_INT >= 11) {
      // Pre API 11, alpha is used in place of scale up to show the
      // progress circle appearing.
      // Don't adjust the alpha during appearance otherwise.
      mProgress.setAlpha(MAX_ALPHA);
    }
    mScaleAnimation = new Animation() {
      @Override public void applyTransformation(float interpolatedTime, Transformation t) {
        setAnimationProgress(interpolatedTime);
      }
    };
    mScaleAnimation.setDuration(mMediumAnimationDuration);
    if (listener != null) {
      mCircleView.setAnimationListener(listener);
    }
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mScaleAnimation);
  }

  /**
   * Pre API 11, this does an alpha animation.
   */
  private void setAnimationProgress(float progress) {
    if (isAlphaUsedForScale()) {
      setColorViewAlpha((int) (progress * MAX_ALPHA));
    } else {
      ViewCompat.setScaleX(mCircleView, progress);
      ViewCompat.setScaleY(mCircleView, progress);
    }
  }

  private void setRefreshing(boolean refreshing, final boolean notify) {
    if (mRefreshing != refreshing) {
      mNotify = notify;
      ensureTarget();
      mRefreshing = refreshing;
      if (mRefreshing) {
        animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
      } else {
        startScaleDownAnimation(mRefreshListener);
      }
    }
  }

  private void startScaleDownAnimation(AnimationListener listener) {
    mScaleDownAnimation = new Animation() {
      @Override public void applyTransformation(float interpolatedTime, Transformation t) {
        setAnimationProgress(1 - interpolatedTime);
      }
    };
    mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
    mCircleView.setAnimationListener(listener);
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mScaleDownAnimation);
  }

  private void startProgressAlphaStartAnimation() {
    mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
  }

  private void startProgressAlphaMaxAnimation() {
    mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
  }

  private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
    // Pre API 11, alpha is used in place of scale. Don't also use it to
    // show the trigger point.
    if (mScale && isAlphaUsedForScale()) {
      return null;
    }
    Animation alpha = new Animation() {
      @Override public void applyTransformation(float interpolatedTime, Transformation t) {
        mProgress.setAlpha(
            (int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
      }
    };
    alpha.setDuration(ALPHA_ANIMATION_DURATION);
    // Clear out the previous animation listeners.
    mCircleView.setAnimationListener(null);
    mCircleView.clearAnimation();
    mCircleView.startAnimation(alpha);
    return alpha;
  }

  /**
   * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
   */
  @Deprecated public void setProgressBackgroundColor(int colorRes) {
    setProgressBackgroundColorSchemeResource(colorRes);
  }

  /**
   * Set the background color of the progress spinner disc.
   *
   * @param colorRes Resource id of the color.
   */
  public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
    setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
  }

  /**
   * Set the background color of the progress spinner disc.
   */
  public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
    mCircleView.setBackgroundColor(color);
    mProgress.setBackgroundColor(color);
  }

  /**
   * Set the color resources used in the progress animation from color resources.
   * The first color will also be the color of the bar that grows in response
   * to a user swipe gesture.
   */
  public void setColorSchemeResources(@ColorRes int... colorResIds) {
    int[] colorRes = new int[colorResIds.length];
    for (int i = 0; i < colorResIds.length; i++) {
      colorRes[i] = ContextCompat.getColor(getContext(), colorResIds[i]);
    }
    setColorSchemeColors(colorRes);
  }

  /**
   * Set the colors used in the progress animation. The first
   * color will also be the color of the bar that grows in response to a user
   * swipe gesture.
   */
  @ColorInt public void setColorSchemeColors(int... colors) {
    ensureTarget();
    mProgress.setColorSchemeColors(colors);
  }

  /**
   * @return Whether the SwipeRefreshWidget is actively showing refresh
   * progress.
   */
  public boolean isRefreshing() {
    return mRefreshing;
  }

  /**
   * Notify the widget that refresh state has changed. Do not call this when
   * refresh is triggered by a swipe gesture.
   *
   * @param refreshing Whether or not the view should show refresh progress.
   */
  public void setRefreshing(boolean refreshing) {
    if (refreshing && mRefreshing != refreshing) {
      // scale and show
      mRefreshing = refreshing;
      int endTarget = 0;
      if (!mUsingCustomStart) {
        switch (mDirection) {
          case BOTTOM:
            endTarget = getMeasuredHeight() - (int) (mSpinnerFinalOffset);
            break;
          case TOP:
          default:
            endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            break;
        }
      } else {
        endTarget = (int) mSpinnerFinalOffset;
      }
      setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true /* requires update */);
      mNotify = false;
      startScaleUpAnimation(mRefreshListener);
    } else {
      setRefreshing(refreshing, false /* notify */);
    }
  }

  private void ensureTarget() {
    // Don't bother getting the parent height if the parent hasn't been laid
    // out yet.
    if (mTarget == null) {
      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        if (!child.equals(mCircleView)) {
          mTarget = child;
          break;
        }
      }
    }
  }

  /**
   * Set the distance to trigger a sync in dips
   */
  public void setDistanceToTriggerSync(int distance) {
    mTotalDragDistance = distance;
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    final int width = getMeasuredWidth();
    final int height = getMeasuredHeight();
    if (getChildCount() == 0) {
      return;
    }
    if (mTarget == null) {
      ensureTarget();
    }
    if (mTarget == null) {
      return;
    }
    final View child = mTarget;
    final int childLeft = getPaddingLeft();
    final int childTop = getPaddingTop();
    final int childWidth = width - getPaddingLeft() - getPaddingRight();
    final int childHeight = height - getPaddingTop() - getPaddingBottom();
    child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    int circleWidth = mCircleView.getMeasuredWidth();
    int circleHeight = mCircleView.getMeasuredHeight();
    mCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
        (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
  }

  @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (mTarget == null) {
      ensureTarget();
    }
    if (mTarget == null) {
      return;
    }
    mTarget.measure(
        MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
            MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
            MeasureSpec.EXACTLY));
    mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(mCircleHeight, MeasureSpec.EXACTLY));

    if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
      mOriginalOffsetCalculated = true;

      switch (mDirection) {
        case BOTTOM:
          mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight();
          break;
        case TOP:
        default:
          mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
          break;
      }
    }

    mCircleViewIndex = -1;
    // Get the index of the circleview.
    for (int index = 0; index < getChildCount(); index++) {
      if (getChildAt(index) == mCircleView) {
        mCircleViewIndex = index;
        break;
      }
    }
  }

  /**
   * Get the diameter of the progress circle that is displayed as part of the
   * swipe to refresh layout. This is not valid until a measure pass has
   * completed.
   *
   * @return Diameter in pixels of the progress circle view.
   */
  public int getProgressCircleDiameter() {
    return mCircleView != null ? mCircleView.getMeasuredHeight() : 0;
  }

  /**
   * @return Whether it is possible for the child view of this layout to
   * scroll up. Override this if the child view is a custom view.
   */
  public boolean canChildScrollUp() {
    if (android.os.Build.VERSION.SDK_INT < 14) {
      if (mTarget instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) mTarget;
        return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
            || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
      } else {
        return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
      }
    } else {
      return ViewCompat.canScrollVertically(mTarget, -1);
    }
  }

  public boolean canChildScrollDown() {
    if (android.os.Build.VERSION.SDK_INT < 14) {
      if (mTarget instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) mTarget;
        try {
          if (absListView.getCount() > 0) {
            if (absListView.getLastVisiblePosition() + 1 == absListView.getCount()) {
              int lastIndex =
                  absListView.getLastVisiblePosition() - absListView.getFirstVisiblePosition();
              return absListView.getChildAt(lastIndex).getBottom()
                  == absListView.getPaddingBottom();
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return true;
      } else {
        return true;
      }
    } else {
      return ViewCompat.canScrollVertically(mTarget, 1);
    }
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    ensureTarget();

    final int action = MotionEventCompat.getActionMasked(ev);

    if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
      mReturningToStart = false;
    }

    switch (mDirection) {
      case BOTTOM:
        if (!isEnabled()
            || mReturningToStart
            || (!mBothDirection && canChildScrollDown())
            || mRefreshing
            || mNestedScrollInProgress) {
          // Fail fast if we're not in a state where a swipe is possible
          return false;
        }
        break;
      case TOP:
      default:
        if (!isEnabled()
            || mReturningToStart
            || (!mBothDirection && canChildScrollUp())
            || mRefreshing
            || mNestedScrollInProgress) {
          // Fail fast if we're not in a state where a swipe is possible
          return false;
        }
        break;
    }

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop(), true);
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        mIsBeingDragged = false;
        final float initialDownY = getMotionEventY(ev, mActivePointerId);
        if (initialDownY == -1) {
          return false;
        }
        mInitialDownY = initialDownY;
        break;

      case MotionEvent.ACTION_MOVE:
        if (mActivePointerId == INVALID_POINTER) {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
          return false;
        }

        final float y = getMotionEventY(ev, mActivePointerId);
        if (y == -1) {
          return false;
        }

        if (mBothDirection) {
          if (y > mInitialDownY) {
            setRawDirection(Direction.TOP);
          } else if (y < mInitialDownY) {
            setRawDirection(Direction.BOTTOM);
          }
          if ((mDirection == Direction.BOTTOM && canChildScrollDown()) || (mDirection
              == Direction.TOP && canChildScrollUp())) {
            mInitialDownY = y;
            return false;
          }
        }

        float yDiff;
        switch (mDirection) {
          case BOTTOM:
            yDiff = mInitialDownY - y;
            break;
          case TOP:
          default:
            yDiff = y - mInitialDownY;
            break;
        }

        if (yDiff > mTouchSlop && !mIsBeingDragged) {
          switch (mDirection) {
            case BOTTOM:
              mInitialMotionY = mInitialDownY - mTouchSlop;
              break;
            case TOP:
            default:
              mInitialMotionY = mInitialDownY + mTouchSlop;
              break;
          }
          mIsBeingDragged = true;
          mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        }
        break;

      case MotionEventCompat.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        mIsBeingDragged = false;
        mActivePointerId = INVALID_POINTER;
        break;
    }

    return mIsBeingDragged;
  }

  // NestedScrollingParent

  private float getMotionEventY(MotionEvent ev, int activePointerId) {
    final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
    if (index < 0) {
      return -1;
    }
    return MotionEventCompat.getY(ev, index);
  }

  @Override public void requestDisallowInterceptTouchEvent(boolean b) {
    // if this is a List < L or another view that doesn't support nested
    // scrolling, ignore this request so that the vertical scroll event
    // isn't stolen
    if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView) || (mTarget
        != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
      // Nope.
    } else {
      super.requestDisallowInterceptTouchEvent(b);
    }
  }

  @Override public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    return isEnabled()
        && canChildScrollUp()
        && !mReturningToStart
        && !mRefreshing
        && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override public void onNestedScrollAccepted(View child, View target, int axes) {
    // Reset the counter of how much leftover scroll needs to be consumed.
    mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    // Dispatch up to the nested parent
    startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
    mTotalUnconsumed = 0;
    mNestedScrollInProgress = true;
  }

  @Override public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
    // before allowing the list to scroll
    if (dy > 0 && mTotalUnconsumed > 0) {
      if (dy > mTotalUnconsumed) {
        consumed[1] = dy - (int) mTotalUnconsumed;
        mTotalUnconsumed = 0;
      } else {
        mTotalUnconsumed -= dy;
        consumed[1] = dy;
      }
      moveSpinner(mTotalUnconsumed);
    }

    // If a client layout is using a custom start position for the circle
    // view, they mean to hide it again before scrolling the child view
    // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
    // the circle so it isn't exposed if its blocking content is moved
    if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0 && Math.abs(dy - consumed[1]) > 0) {
      mCircleView.setVisibility(View.GONE);
    }

    // Now let our nested parent consume the leftovers
    final int[] parentConsumed = mParentScrollConsumed;
    if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
      consumed[0] += parentConsumed[0];
      consumed[1] += parentConsumed[1];
    }
  }

  @Override public int getNestedScrollAxes() {
    return mNestedScrollingParentHelper.getNestedScrollAxes();
  }

  // NestedScrollingChild

  @Override public void onStopNestedScroll(View target) {
    mNestedScrollingParentHelper.onStopNestedScroll(target);
    mNestedScrollInProgress = false;
    // Finish the spinner for nested scrolling if we ever consumed any
    // unconsumed nested scroll
    if (mTotalUnconsumed > 0) {
      finishSpinner(mTotalUnconsumed);
      mTotalUnconsumed = 0;
    }
    // Dispatch up our nested parent
    stopNestedScroll();
  }

  @Override
  public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
      final int dxUnconsumed, final int dyUnconsumed) {
    // Dispatch up to the nested parent first
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

    // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
    // sometimes between two nested scrolling views, we need a way to be able to know when any
    // nested scrolling parent has stopped handling events. We do that by using the
    // 'offset in window 'functionality to see if we have been moved from the event.
    // This is a decent indication of whether we should take over the event stream or not.
    final int dy = dyUnconsumed + mParentOffsetInWindow[1];
    if (dy < 0) {
      mTotalUnconsumed += Math.abs(dy);
      moveSpinner(mTotalUnconsumed);
    }
  }

  @Override public boolean isNestedScrollingEnabled() {
    return mNestedScrollingChildHelper.isNestedScrollingEnabled();
  }

  @Override public void setNestedScrollingEnabled(boolean enabled) {
    mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
  }

  @Override public boolean startNestedScroll(int axes) {
    return mNestedScrollingChildHelper.startNestedScroll(axes);
  }

  @Override public void stopNestedScroll() {
    mNestedScrollingChildHelper.stopNestedScroll();
  }

  @Override public boolean hasNestedScrollingParent() {
    return mNestedScrollingChildHelper.hasNestedScrollingParent();
  }

  @Override public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed, int[] offsetInWindow) {
    return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed, offsetInWindow);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  @Override public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    return dispatchNestedPreFling(velocityX, velocityY);
  }

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    return dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }

  private boolean isAnimationRunning(Animation animation) {
    return animation != null && animation.hasStarted() && !animation.hasEnded();
  }

  private void moveSpinner(float overscrollTop) {
    mProgress.showArrow(true);
    float originalDragPercent = overscrollTop / mTotalDragDistance;

    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
    float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
    float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
    float slingshotDist =
        mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop : mSpinnerFinalOffset;
    float tensionSlingshotPercent =
        Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
    float tensionPercent =
        (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
    float extraMove = (slingshotDist) * tensionPercent * 2;

    int targetY;
    if (mDirection == Direction.TOP) {
      targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
    } else {
      targetY = mOriginalOffsetTop - (int) ((slingshotDist * dragPercent) + extraMove);
    }

    // where 1.0f is a full circle
    if (mCircleView.getVisibility() != View.VISIBLE) {
      mCircleView.setVisibility(View.VISIBLE);
    }
    if (!mScale) {
      ViewCompat.setScaleX(mCircleView, 1f);
      ViewCompat.setScaleY(mCircleView, 1f);
    }

    if (mScale) {
      setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
    }
    if (overscrollTop < mTotalDragDistance) {
      if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA && !isAnimationRunning(
          mAlphaStartAnimation)) {
        // Animate the alpha
        startProgressAlphaStartAnimation();
      }
    } else {
      if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
        // Animate the alpha
        startProgressAlphaMaxAnimation();
      }
    }
    float strokeStart = adjustedPercent * .8f;
    mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
    mProgress.setArrowScale(Math.min(1f, adjustedPercent));

    float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
    mProgress.setProgressRotation(rotation);
    setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
  }

  private void finishSpinner(float overscrollTop) {
    if (overscrollTop > mTotalDragDistance) {
      setRefreshing(true, true /* notify */);
    } else {
      // cancel refresh
      mRefreshing = false;
      mProgress.setStartEndTrim(0f, 0f);
      AnimationListener listener = null;
      if (!mScale) {
        listener = new AnimationListener() {

          @Override public void onAnimationStart(Animation animation) {
          }

          @Override public void onAnimationEnd(Animation animation) {
            if (!mScale) {
              startScaleDownAnimation(null);
            }
          }

          @Override public void onAnimationRepeat(Animation animation) {
          }
        };
      }
      animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
      mProgress.showArrow(false);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    final int action = MotionEventCompat.getActionMasked(ev);
    int pointerIndex = -1;

    if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
      mReturningToStart = false;
    }

    switch (mDirection) {
      case BOTTOM:
        if (!isEnabled()
            || mReturningToStart
            || canChildScrollDown()
            || mRefreshing
            || mNestedScrollInProgress) {
          // Fail fast if we're not in a state where a swipe is possible
          return false;
        }
        break;
      case TOP:
      default:
        if (!isEnabled()
            || mReturningToStart
            || canChildScrollUp()
            || mRefreshing
            || mNestedScrollInProgress) {
          // Fail fast if we're not in a state where a swipe is possible
          return false;
        }
        break;
    }

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        mIsBeingDragged = false;
        break;

      case MotionEvent.ACTION_MOVE: {
        pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        if (pointerIndex < 0) {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
          return false;
        }

        final float y = MotionEventCompat.getY(ev, pointerIndex);

        float overscrollTop;
        switch (mDirection) {
          case BOTTOM:
            overscrollTop = (mInitialMotionY - y) * DRAG_RATE;
            break;
          case TOP:
          default:
            overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
            break;
        }

        if (mIsBeingDragged) {
          if (overscrollTop > 0) {
            moveSpinner(overscrollTop);
          } else {
            return false;
          }
        }
        break;
      }
      case MotionEventCompat.ACTION_POINTER_DOWN: {
        pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (pointerIndex < 0) {
          Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
          return false;
        }
        mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        break;
      }

      case MotionEventCompat.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;

      case MotionEvent.ACTION_UP: {
        pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        if (pointerIndex < 0) {
          Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
          return false;
        }

        final float y = MotionEventCompat.getY(ev, pointerIndex);
        float overscrollTop;
        switch (mDirection) {
          case BOTTOM:
            overscrollTop = (mInitialMotionY - y) * DRAG_RATE;
            break;
          case TOP:
          default:
            overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
            break;
        }

        mIsBeingDragged = false;
        finishSpinner(overscrollTop);
        mActivePointerId = INVALID_POINTER;
        return false;
      }
      case MotionEvent.ACTION_CANCEL:
        return false;
    }

    return true;
  }

  private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
    mFrom = from;
    mAnimateToCorrectPosition.reset();
    mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
    mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
    if (listener != null) {
      mCircleView.setAnimationListener(listener);
    }
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mAnimateToCorrectPosition);
  }

  private void animateOffsetToStartPosition(int from, AnimationListener listener) {
    if (mScale) {
      // Scale the item back down
      startScaleDownReturnToStartAnimation(from, listener);
    } else {
      mFrom = from;
      mAnimateToStartPosition.reset();
      mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
      mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
      if (listener != null) {
        mCircleView.setAnimationListener(listener);
      }
      mCircleView.clearAnimation();
      mCircleView.startAnimation(mAnimateToStartPosition);
    }
  }

  private void moveToStart(float interpolatedTime) {
    int targetTop = 0;
    targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
    int offset = targetTop - mCircleView.getTop();
    setTargetOffsetTopAndBottom(offset, false /* requires update */);
  }

  private void startScaleDownReturnToStartAnimation(int from, AnimationListener listener) {
    mFrom = from;
    if (isAlphaUsedForScale()) {
      mStartingScale = mProgress.getAlpha();
    } else {
      mStartingScale = ViewCompat.getScaleX(mCircleView);
    }
    mScaleDownToStartAnimation = new Animation() {
      @Override public void applyTransformation(float interpolatedTime, Transformation t) {
        float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
        setAnimationProgress(targetScale);
        moveToStart(interpolatedTime);
      }
    };
    mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
    if (listener != null) {
      mCircleView.setAnimationListener(listener);
    }
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mScaleDownToStartAnimation);
  }

  private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
    mCircleView.bringToFront();
    mCircleView.offsetTopAndBottom(offset);
    mCurrentTargetOffsetTop = mCircleView.getTop();
    if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
      invalidate();
    }
  }

  private void onSecondaryPointerUp(MotionEvent ev) {
    final int pointerIndex = MotionEventCompat.getActionIndex(ev);
    final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
    if (pointerId == mActivePointerId) {
      // This was our active pointer going up. Choose a new
      // active pointer and adjust accordingly.
      final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
      mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
    }
  }

  public Direction getDirection() {
    return mBothDirection ? Direction.BOTH : mDirection;
  }

  public void setDirection(Direction direction) {
    if (direction == Direction.BOTH) {
      mBothDirection = true;
    } else {
      mBothDirection = false;
      mDirection = direction;
    }
    switch (mDirection) {
      case BOTTOM:
        mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight();
        break;
      case TOP:
      default:
        mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
        break;
    }
  }

  // only TOP or Bottom
  private void setRawDirection(Direction direction) {
    if (mDirection == direction) {
      return;
    }
    mDirection = direction;
    switch (mDirection) {
      case BOTTOM:
        mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight();
        break;
      case TOP:
      default:
        mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
        break;
    }
  }

  public enum Direction {
    TOP(0), BOTTOM(1), BOTH(2);
    private static final Map<Integer, Direction> STRING_MAPPING = new HashMap<>();

    static {
      for (Direction direction : Direction.values()) {
        STRING_MAPPING.put(direction.mValue, direction);
      }
    }

    private int mValue;

    Direction(int value) {
      this.mValue = value;
    }

    public static Direction fromValue(int value) {
      return STRING_MAPPING.get(value);
    }
  }

  /**
   * Classes that wish to be notified when the swipe gesture correctly
   * triggers a refresh should implement this interface.
   */
  public interface OnRefreshListener {
    void onRefresh(Direction direction);
  }
}
