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
import support.ui.utilities.BusProvider;

public class DefaultEmptyView extends FrameLayout implements EmptyView, View.OnClickListener {

  private ImageView imageView;
  private TextView titleTextView;
  private TextView subtitleTextView;

  public DefaultEmptyView(Context context) {
    super(context);
    initialize(context);
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.support_ui_view_empty, this, false);
    addView(view);
    setOnClickListener(this);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    imageView = ButterKnife.findById(this, R.id.support_ui_empty_image_view);
    titleTextView = ButterKnife.findById(this, R.id.support_ui_empty_title);
    subtitleTextView = ButterKnife.findById(this, R.id.support_ui_empty_subtitle);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    imageView = null;
    titleTextView = null;
    subtitleTextView = null;
  }

  public DefaultEmptyView buildImageView(@DrawableRes int drawableRes) {
    if (imageView != null) {
      imageView.setImageResource(drawableRes);
    }
    return this;
  }

  public DefaultEmptyView buildTitle(@StringRes int stringRes) {
    return buildTitle(getContext().getString(stringRes));
  }

  public DefaultEmptyView buildTitle(String title) {
    if (titleTextView != null) {
      titleTextView.setText(title);
    }
    return this;
  }

  public DefaultEmptyView buildSubtitle(@StringRes int stringRes) {
    return buildSubtitle(getContext().getString(stringRes));
  }

  public DefaultEmptyView buildSubtitle(String subtitle) {
    if (subtitleTextView != null) {
      subtitleTextView.setText(subtitle);
    }
    return this;
  }

  @Override public void onClick(View v) {
    BusProvider.getInstance().post(produceRefresh());
  }

  public RefreshEvent produceRefresh() {
    return new RefreshEvent();
  }
}
