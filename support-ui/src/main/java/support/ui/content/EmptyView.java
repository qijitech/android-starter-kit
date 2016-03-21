package support.ui.content;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public interface EmptyView {
  EmptyView buildImageView(@DrawableRes int drawableRes);

  EmptyView buildTitle(@StringRes int stringRes);

  EmptyView buildTitle(String title);

  EmptyView buildSubtitle(@StringRes int stringRes);

  EmptyView buildSubtitle(String subtitle);
}
