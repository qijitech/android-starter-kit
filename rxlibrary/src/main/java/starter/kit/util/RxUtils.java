package starter.kit.util;

import android.content.Context;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public final class RxUtils {

  private RxUtils() {

  }

  public static <T> ObservableTransformer<T, T> progressTransformer(
      final NetworkContract.ProgressInterface progress) {
    return new ObservableTransformer<T, T>() {
      @Override public ObservableSource<T> apply(Observable<T> observable) {
        return observable.doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(@NonNull Disposable disposable) throws Exception {
            progress.showProgress();
          }
        }).subscribeOn(mainThread()).doOnTerminate(new Action() {
          @Override public void run() throws Exception {
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
  public static <T> ObservableTransformer<T, T> hudTransformer(
      final NetworkContract.HudInterface hud) {
    return new ObservableTransformer<T, T>() {
      @Override public ObservableSource<T> apply(Observable<T> observable) {
        return observable.doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(@NonNull Disposable disposable) throws Exception {
            hud.showHud();
          }
        }).subscribeOn(mainThread()).doOnTerminate(new Action() {
          @Override public void run() {
            RxUtils.dismissHud();
          }
        }).observeOn(mainThread());
      }
    };
  }

  /**
   * 隐藏Hud
   *
   * @return Subscription
   */
  public static Disposable dismissHud() {
    return RxUtils.empty(new Action() {
      @Override public void run() {
        Hud.getInstance().dismissHud();
      }
    });
  }

  /**
   * 显示Hud, 不可以取消
   *
   * @param context theme context
   * @param message message with hud
   * @return Subscription
   */
  public static Disposable showHud(final Context context, String message) {
    return Observable.just(message).observeOn(mainThread()).subscribe(new Consumer<String>() {
      @Override public void accept(@NonNull String msg) throws Exception {
        Hud.getInstance().showHud(context, msg);
      }
    });
  }

  /**
   * 显示Hud,可以取消
   *
   * @param context theme context
   * @param message message with hud
   * @param callback dismiss callback
   * @return Subscription
   */
  public static Disposable showHud(final Context context, String message, final Hud.HudCallback callback) {
    return Observable.just(message).observeOn(mainThread()).subscribe(new Consumer<String>() {
      @Override public void accept(String msg) {
        Hud.getInstance().showHud(context, msg, callback);
      }
    });
  }

  public static Disposable empty(Action onTerminate) {
    return Observable.empty().observeOn(mainThread()).doOnTerminate(onTerminate).subscribe();
  }

  /**
   * unsubscribe Subscription
   *
   * @param subscription Subscription
   */
  public static void unsubscribe(Disposable disposable) {
    if (disposable != null && disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
