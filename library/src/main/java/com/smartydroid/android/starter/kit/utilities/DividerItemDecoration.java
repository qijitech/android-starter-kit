package com.smartydroid.android.starter.kit.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private static final int[] ATTRS = { android.R.attr.listDivider };

  private Drawable divider;
  private int insets;
  private Context context;
  private boolean addInsets = false;

  public DividerItemDecoration(Context context, Drawable divider) {
    this.context = context;
    this.divider = divider;
  }

  public DividerItemDecoration(Context context) {
    this.context = context;
    TypedArray a = context.obtainStyledAttributes(ATTRS);
    divider = a.getDrawable(0);
    a.recycle();
  }

  public void setDivider(@DrawableRes int dividerDrawableRes) {
    divider = context.getResources().getDrawable(dividerDrawableRes);
  }

  public void setInsets(@DimenRes int insets) {
    addInsets = true;
    this.insets = context.getResources().getDimensionPixelSize(insets);
  }

  @Override public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    drawHorizontal(canvas, parent);
    drawToTheRightOfEachChildren(canvas, parent);
  }

  public void drawHorizontal(Canvas c, RecyclerView parent) {
    int childCount = parent.getChildCount();
    if (childCount == 0) return;
    int left, top, right, bottom;
    View currentChild;
    for (int i = 0; i < childCount; i++) {
      currentChild = parent.getChildAt(i);
      int currentChildBottom = currentChild.getBottom();
      if (currentChildBottom > parent.getPaddingTop()
          && currentChildBottom < parent.getBottom() - parent.getPaddingBottom()) {
        left = currentChild.getLeft();
        right = currentChild.getRight();
        top = currentChildBottom;
        bottom = top + divider.getIntrinsicHeight();
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    }
  }

  public void drawToTheRightOfEachChildren(Canvas c, RecyclerView parent) {
    if (parent.getChildCount() == 0) return;
    int top, left, right, bottom;
    int childCount = parent.getChildCount();
    View currentChild;
    for (int i = 0; i < childCount; i++) {
      currentChild = parent.getChildAt(i);
      int offset = 2;
      if (currentChild.getRight() < parent.getRight() - parent.getPaddingRight() - offset) {
        //Avoid drawing over top padding
        top = currentChild.getTop();
        if (top < parent.getPaddingTop()) {
          top = parent.getPaddingTop();
        }
        bottom = currentChild.getBottom() + divider.getIntrinsicHeight();
        //Avoid drawing over bottom padding
        int parentBottom = parent.getBottom() - parent.getPaddingBottom();
        if (bottom > parentBottom) {
          bottom = parentBottom;
        }
        left = currentChild.getRight();
        right = left + divider.getIntrinsicWidth();
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    //We can supply forced insets for each item view here in the Rect
    if (addInsets) {
      outRect.set(insets, insets, insets, insets);
    }
  }
}
