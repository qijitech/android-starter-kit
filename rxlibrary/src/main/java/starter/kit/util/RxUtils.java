package starter.kit.util;

import android.content.Context;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public final class RxUtils {

  private RxUtils() {

  }

  public static <T> Observable.Transformer<T, T> progressTransformer(
      final NetworkContract.ProgressInterface progress) {
    return new Observable.Transformer<T, T>() {
      @Override public Observable<T> call(Observable<T> observable) {
        return observable.doOnSubscribe(new Action0() {
          @Override public void call() {
            progress.showProgress();
          }
        }).subscribeOn(mainThread()).doOnTerminate(new Action0() {
          @Override public void call() {
            progress.hideProgress();
          }
        }).observeOn(mainThread());
      }
    };
  }

  /**
   * 显示并隐藏Hud
   *
   * @param hud HudInterface
   * @param <T> the type of the items emitted by the Observable
   * @return Observable
   */
  public static <T> Observable.Transformer<T, T> hudTransformer(final NetworkContract.HudInterface hud) {
    return new Observable.Transformer<T, T>() {
      @Override public Observable<T> call(Observable<T> observable) {
        return observable.doOnSubscribe(new Action0() {
          @Override public void call() {
            hud.showHud();
          }
        }).subscribeOn(mainThread()).doOnTerminate(new Action0() {
          @Override public void call() {
            RxUtils.dismissHud();
          }
        }).observeOn(mainThread());
      }
    };
  }

  /**
   * 隐藏Hud
   * @return Subscription
   */
  public static Subscription dismissHud() {
    return  RxUtils.empty(new Action0() {
      @Override public void call() {
        Hud.getInstance().dismissHud();
      }
    });
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
        .subscribe(new Action1<String>() {
          @Override public void call(String msg) {
            Hud.getInstance().showHud(context, message);
          }
        });
  }

  /**
   * 显示Hud,可以取消
   * @param context theme context
   * @param message message with hud
   * @param callback dismiss callback
   * @return Subscription
   */
  public static Subscription showHud(Context context, String message, Hud.HudCallback callback) {
    return Observable.just(message)
        .observeOn(mainThread())
        .subscribe(new Action1<String>() {
          @Override public void call(String msg) {
            Hud.getInstance().showHud(context, message, callback);
          }
        });
  }

  public static Subscription empty(Action0 action0) {
    return Observable.empty()
        .observeOn(mainThread())
        .doOnTerminate(action0)
        .subscribe();
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
