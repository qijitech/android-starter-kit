package support.ui.content;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import support.ui.R;
import support.ui.utilities.ViewUtils;

public class DefaultEmptyView extends FrameLayout implements EmptyView, View.OnClickListener {

  private ImageView imageView;
  private TextView titleTextView;
  private TextView subtitleTextView;

  private OnEmptyClickListener listener;

  public DefaultEmptyView(Context context) {
    super(context);
    initialize(context);
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.support_ui_view_empty, this, false);
    addView(view);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    setOnClickListener(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    setOnClickListener(null);
    listener = null;
  }

  public DefaultEmptyView buildImageView(@DrawableRes int drawableRes) {
    if (imageView() != null) {
      imageView().setImageResource(drawableRes);
    }
    return this;
  }

  public DefaultEmptyView buildTitle(@StringRes int stringRes) {
    return buildTitle(getContext().getString(stringRes));
  }

  public DefaultEmptyView buildTitle(String title) {
    if (titleTextView() != null) {
      titleTextView().setText(title);
    }
    return this;
  }

  public DefaultEmptyView buildSubtitle(@StringRes int stringRes) {
    return buildSubtitle(getContext().getString(stringRes));
  }

  public DefaultEmptyView buildSubtitle(String subtitle) {
    if (subtitleTextView() != null) {
      subtitleTextView().setText(subtitle);
    }
    return this;
  }

  @Override public EmptyView shouldDisplayEmptySubtitle(boolean display) {
    ViewUtils.setGone(subtitleTextView(), !display);
    return this;
  }

  @Override public EmptyView shouldDisplayEmptyTitle(boolean display) {
    ViewUtils.setGone(titleTextView(), !display);
    return this;
  }

  @Override public EmptyView shouldDisplayImageView(boolean display) {
    ViewUtils.setGone(imageView(), !display);
    return this;
  }

  public TextView titleTextView() {
    if (titleTextView == null) {
      titleTextView = ButterKnife.findById(this, R.id.support_ui_empty_title);
    }
    return titleTextView;
  }

  public TextView subtitleTextView() {
    if (subtitleTextView == null) {
      subtitleTextView = ButterKnife.findById(this, R.id.support_ui_empty_subtitle);
    }
    return subtitleTextView;
  }

  public ImageView imageView() {
    if (imageView == null) {
      imageView = ButterKnife.findById(this, R.id.support_ui_empty_image_view);
    }
    return imageView;
  }

  @Override public void onClick(View v) {
    if (listener != null) {
      listener.onEmptyClick(v);
    }
  }

  @Override public void setOnEmptyClickListener(OnEmptyClickListener listener) {
    this.listener = listener;
  }
}
