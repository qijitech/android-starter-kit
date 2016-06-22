package starter.kit.rx.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import icepick.Icepick;
import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusSupportFragment;

public abstract class RxStarterFragment<P extends Presenter> extends NucleusSupportFragment<P> {

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
    ButterKnife.bind(this, view);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }
}
