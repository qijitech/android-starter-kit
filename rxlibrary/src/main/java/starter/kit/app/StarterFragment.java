package starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusSupportFragment;
import starter.kit.util.StarterCommon;


/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class StarterFragment<P extends Presenter> extends NucleusSupportFragment<P> {

  private Unbinder mUnbinder;
  private StarterCommon starterCommon;

  protected abstract int getFragmentLayout();

  @Override public void onCreate(Bundle bundle) {
    final PresenterFactory<P> superFactory = super.getPresenterFactory();
    setPresenterFactory(superFactory == null ? null : new PresenterFactory<P>() {
      @Override public P createPresenter() {
        return superFactory.createPresenter();
      }
    });
    super.onCreate(bundle);
    Icepick.restoreInstanceState(this, bundle);

    starterCommon = StarterCommon.create(getActivity());
  }

  @Override public void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    Icepick.saveInstanceState(this, bundle);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(getFragmentLayout(), container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mUnbinder = ButterKnife.bind(this, view);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (mUnbinder != null) {
      mUnbinder.unbind();
      mUnbinder = null;
    }
  }

  @Override public void onDestroy() {
    if (starterCommon != null) {
      starterCommon.onDestroy();
      starterCommon = null;
    }
    super.onDestroy();
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      hideSoftInputMethod();
    }
  }

  public void hideSoftInputMethod() {
    if (starterCommon != null) {
      starterCommon.hideSoftInputMethod();
    }
  }

  public void showSoftInputMethod() {
    if (starterCommon != null) {
      starterCommon.showSoftInputMethod();
    }
  }

  public boolean isImmActive() {
    return starterCommon != null && starterCommon.isImmActive();
  }
}
