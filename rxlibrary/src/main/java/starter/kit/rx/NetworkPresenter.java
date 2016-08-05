package starter.kit.rx;

import android.os.Bundle;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import starter.kit.retrofit.RetrofitException;
import starter.kit.rx.util.HudInterface;
import starter.kit.rx.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public abstract class NetworkPresenter<T, ViewType extends NetworkContract.View> extends RxStarterPresenter<ViewType> implements
    HudInterface {

  private static final int RESTARTABLE_ID = 2000;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(restartableId(), new Func0<Observable<T>>() {
      @Override public Observable<T> call() {
        return request().subscribeOn(io())
            .compose(RxUtils.hudTransformer(NetworkPresenter.this))
            .observeOn(mainThread());
      }
    }, new Action2<ViewType, T>() {
      @Override public void call(ViewType viewType, T item) {
        viewType.onSuccess(item);
      }
    }, new Action2<ViewType, Throwable>() {
      @Override public void call(ViewType viewType, Throwable throwable) {
        RetrofitException error = (RetrofitException) throwable;
        viewType.onError(error);
      }
    });
  }

  public int restartableId() {
    return RESTARTABLE_ID;
  }

  public abstract Observable<T> request();

  public void start() {
    start(restartableId());
  }

  public void stop() {
    stop(restartableId());
  }
}
