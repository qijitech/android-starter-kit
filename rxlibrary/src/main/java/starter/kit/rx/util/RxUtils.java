package starter.kit.rx.util;

import android.content.Context;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import work.wanghao.simplehud.SimpleHUD;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static work.wanghao.simplehud.SimpleHUD.showLoadingMessage;

public final class RxUtils {

  private RxUtils() {

  }


  public static <T> Observable.Transformer<T, T> progressTransformer(final ProgressInterface progress) {
    return observable -> observable.doOnSubscribe(progress::showProgress)
        .subscribeOn(mainThread())
        .doOnTerminate(progress::hideProgress)
        .observeOn(mainThread());
  }

  /**
   * 显示并隐藏Hud
   *
   * @param hud HudInterface
   * @param <T> the type of the items emitted by the Observable
   * @return Observable
   */
  public static <T> Observable.Transformer<T, T> hudTransformer(final HudInterface hud) {
    return observable -> observable.doOnSubscribe(hud::showHud)
        .subscribeOn(mainThread())
        .doOnTerminate(RxUtils::dismissHud)
        .observeOn(mainThread());
  }

  /**
   * 隐藏Hud
   * @return Subscription
   */
  public static Subscription dismissHud() {
    return Observable.empty()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate(SimpleHUD::dismiss)
        .subscribe();
  }

  /**
   * 显示Hud, 不可以取消
   * @param context theme context
   * @param message message with hud
   * @return Subscription
   */
  public static Subscription showHud(Context context, String message) {
    return Observable.just(message)
        .observeOn(mainThread())
        .subscribe(msg -> {
          showLoadingMessage(context, msg, false);
        });
  }

  /**
   * 显示Hud,可以取消
   * @param context theme context
   * @param message message with hud
   * @param callback dismiss callback
   * @return Subscription
   */
  public static Subscription showHud(Context context, String message,
      SimpleHUD.SimpleHUDCallback callback) {
    return Observable.just(message)
        .observeOn(mainThread())
        .subscribe(msg -> {
          showLoadingMessage(context, msg, true, callback);
        });
  }

  /**
   * unsubscribe Subscription
   * @param subscription Subscription
   */
  public static void unsubscribe(Subscription subscription) {
    if (subscription != null && subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}
