package starter.kit.app;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import nucleus.presenter.Presenter;
import starter.kit.rx.R;

/**
 * A {@link StarterActivity} that simply contains a single fragment. The intent used to invoke this
 * activity is forwarded to the fragment as arguments during fragment instantiation. Derived
 * activities should only need to implement {@link SimpleSinglePaneActivity#onCreatePane()}.
 */
public abstract class SinglePaneActivity<P extends Presenter> extends StarterActivity<P> {

  private Fragment mFragment;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentViewResId());

    if (getIntent().hasExtra(Intent.EXTRA_TITLE)) {
      setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
    }

    final String customTitle = getIntent().getStringExtra(Intent.EXTRA_TITLE);
    setTitle(customTitle != null ? customTitle : getTitle());

    if (savedInstanceState == null) {
      mFragment = onCreatePane();
      mFragment.setArguments(intentToFragmentArguments(getIntent()));
      getSupportFragmentManager().beginTransaction()
          .add(R.id.rootContainer, mFragment, "single_pane")
          .commit();
    } else {
      mFragment = getSupportFragmentManager().findFragmentByTag("single_pane");
    }
  }

  protected int getContentViewResId() {
    return R.layout.support_ui_singlepane_empty;
  }

  /**
   * Called in <code>onCreate</code> when the fragment constituting this activity is needed.
   * The returned fragment's arguments will be set to the intent used to invoke this activity.
   */
  protected abstract Fragment onCreatePane();

  public Fragment getFragment() {
    return mFragment;
  }
}
