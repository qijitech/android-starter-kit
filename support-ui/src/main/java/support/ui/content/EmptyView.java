package support.ui.content;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;

public interface EmptyView {
  EmptyView buildImageView(@DrawableRes int drawableRes);

  EmptyView buildTitle(@StringRes int stringRes);

  EmptyView buildTitle(String title);

  EmptyView buildSubtitle(@StringRes int stringRes);

  EmptyView buildSubtitle(String subtitle);

  EmptyView shouldDisplayEmptySubtitle(boolean display);

  EmptyView shouldDisplayEmptyTitle(boolean display);

  EmptyView shouldDisplayImageView(boolean display);

  void setOnEmptyClickListener(OnEmptyClickListener listener);

  interface OnEmptyClickListener {
    void onEmptyClick(View view);
  }
}
