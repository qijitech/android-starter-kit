package starter.kit.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusAppCompatActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import starter.kit.util.StarterCommon;

public class StarterActivity<P extends Presenter> extends NucleusAppCompatActivity<P> {

  private StarterCommon starterCommon;
  private Unbinder mUnbinder;

  private final CompositeSubscription subscriptions = new CompositeSubscription();

  public void add(Subscription subscription) {
    subscriptions.add(subscription);
  }

  public void remove(Subscription subscription) {
    subscriptions.remove(subscription);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    starterCommon = StarterCommon.create(this);
  }

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    mUnbinder = ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mUnbinder != null) {
      mUnbinder.unbind();
      mUnbinder = null;
    }

    if (starterCommon != null) {
      starterCommon.onDestroy();
      starterCommon = null;
    }

    subscriptions.unsubscribe();
  }

  @Override public void finish() {
    hideSoftInputMethod();
    super.finish();
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

  /**
   * Converts an intent into a {@link android.os.Bundle} suitable for use as fragment arguments.
   */
  public static Bundle intentToFragmentArguments(Intent intent) {
    Bundle arguments = new Bundle();
    if (intent == null) {
      return arguments;
    }

    final Uri data = intent.getData();
    if (data != null) {
      arguments.putParcelable("_uri", data);
    }

    final Bundle extras = intent.getExtras();
    if (extras != null) {
      arguments.putAll(intent.getExtras());
    }

    return arguments;
  }

  /**
   * Converts a fragment arguments bundle into an intent.
   */
  public static Intent fragmentArgumentsToIntent(Bundle arguments) {
    Intent intent = new Intent();
    if (arguments == null) {
      return intent;
    }

    final Uri data = arguments.getParcelable("_uri");
    if (data != null) {
      intent.setData(data);
    }

    intent.putExtras(arguments);
    intent.removeExtra("_uri");
    return intent;
  }
}
