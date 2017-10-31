package nucleus5.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import nucleus5.factory.PresenterFactory;
import nucleus5.factory.PresenterStorage;
import nucleus5.presenter.Presenter;

/**
 * This class adopts a View lifecycle to the Presenter`s lifecycle.
 * presenter生命周期委托
 *
 * @param <P> a type of the presenter.
 */
public final class PresenterLifecycleDelegate<P extends Presenter> {

  private static final String PRESENTER_KEY = "presenter";
  private static final String PRESENTER_ID_KEY = "presenter_id";
  /**
   * presenter工厂类，生成指定类型的presenter
   */
  @Nullable private PresenterFactory<P> presenterFactory;
  /**
   * presenter实例
   */
  @Nullable private P presenter;
  @Nullable private Bundle bundle;
  /**
   * presenter是否已绑定视图
   */
  private boolean presenterHasView;

  public PresenterLifecycleDelegate(@Nullable PresenterFactory<P> presenterFactory) {
    this.presenterFactory = presenterFactory;
  }

  /**
   * {@link ViewWithPresenter#getPresenterFactory()}
   */
  @Nullable public PresenterFactory<P> getPresenterFactory() {
    return presenterFactory;
  }

  /**
   * {@link ViewWithPresenter#setPresenterFactory(PresenterFactory)}
   * 修改默认的PresenterFactory
   */
  public void setPresenterFactory(@Nullable PresenterFactory<P> presenterFactory) {
    if (presenter != null) {
      throw new IllegalArgumentException(
          "setPresenterFactory() should be called before onResume()");
    }
    this.presenterFactory = presenterFactory;
  }

  /**
   * {@link ViewWithPresenter#getPresenter()}
   */
  public P getPresenter() {
    if (presenterFactory != null) {
      if (presenter == null && bundle != null) {
        //有保存的presenter，不重新进行创建而是获取保存的Presenter
        presenter = PresenterStorage.INSTANCE.getPresenter(bundle.getString(PRESENTER_ID_KEY));
      }
      if (presenter == null) {
        presenter = presenterFactory.createPresenter();
        PresenterStorage.INSTANCE.add(presenter);
        //调用Presenter初始化创建方法
        presenter.create(bundle == null ? null : bundle.getBundle(PRESENTER_KEY));
      }
      bundle = null;
    }
    return presenter;
  }

  /**
   * {@link android.app.Activity#onSaveInstanceState(Bundle)}, {@link
   * android.app.Fragment#onSaveInstanceState(Bundle)}, {@link android.view.View#onSaveInstanceState()}.
   * 保存界面被回收前的数据
   */
  public Bundle onSaveInstanceState() {
    Bundle bundle = new Bundle();
    getPresenter();
    if (presenter != null) {
      Bundle presenterBundle = new Bundle();
      /* 调用presenter对应的保存数据方法，界面如果需要重新恢复数据，则界面
             对应的presenter应复写此方法 */
      presenter.save(presenterBundle);
      bundle.putBundle(PRESENTER_KEY, presenterBundle);
      bundle.putString(PRESENTER_ID_KEY, PresenterStorage.INSTANCE.getId(presenter));
    }
    return bundle;
  }

  /**
   * {@link android.app.Activity#onCreate(Bundle)}, {@link android.app.Fragment#onCreate(Bundle)},
   * {@link android.view.View#onRestoreInstanceState(Parcelable)}.
   */
  public void onRestoreInstanceState(Bundle presenterState) {
    if (presenter != null) {
      throw new IllegalArgumentException(
          "onRestoreInstanceState() should be called before onResume()");
    }
    this.bundle = ParcelFn.unmarshall(ParcelFn.marshall(presenterState));
  }

  /**
   * {@link android.app.Activity#onResume()},
   * {@link android.app.Fragment#onResume()},
   * {@link android.view.View#onAttachedToWindow()}
   */
  public void onResume(Object view) {
    getPresenter();
    if (presenter != null && !presenterHasView) {
      //绑定view与presenter
      //noinspection unchecked
      presenter.takeView(view);
      presenterHasView = true;
    }
  }

  /**
   * {@link android.app.Activity#onDestroy()},
   * {@link android.app.Fragment#onDestroyView()},
   * {@link android.view.View#onDetachedFromWindow()}
   */
  public void onDropView() {
    if (presenter != null && presenterHasView) {
      //解绑view与presenter
      presenter.dropView();
      presenterHasView = false;
    }
  }

  /**
   * {@link android.app.Activity#onDestroy()},
   * {@link android.app.Fragment#onDestroy()},
   * {@link android.view.View#onDetachedFromWindow()}
   */
  public void onDestroy(boolean isFinal) {
    if (presenter != null && isFinal) {
      presenter.destroy();
      presenter = null;
    }
  }
}
