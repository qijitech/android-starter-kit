package starter.kit.rx.app;

import butterknife.ButterKnife;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusAppCompatActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RxStarterActivity<P extends Presenter> extends NucleusAppCompatActivity<P> {

  private final CompositeSubscription subscriptions = new CompositeSubscription();

  public void add(Subscription subscription) {
    subscriptions.add(subscription);
  }

  public void remove(Subscription subscription) {
    subscriptions.remove(subscription);
  }

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
    subscriptions.unsubscribe();
  }
}
