package starter.kit.views;

import android.content.Context;
import starter.kit.rx.R;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class DefaultEmptyView extends BaseEmptyView {

  public DefaultEmptyView(Context context) {
    super(context);
  }

  @Override protected int getLayout() {
    return R.layout.default_view_empty;
  }
}
