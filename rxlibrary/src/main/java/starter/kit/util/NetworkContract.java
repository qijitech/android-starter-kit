package starter.kit.util;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public interface NetworkContract {

  interface View<T> {
    void onSuccess(T data);

    void onError(Throwable throwable);
  }

  interface HudInterface {
    void showHud();
  }

  interface ProgressInterface {
    void showProgress();

    void hideProgress();
  }

  interface ContentInterface<T> extends ProgressInterface, View<T> {
  }
}
