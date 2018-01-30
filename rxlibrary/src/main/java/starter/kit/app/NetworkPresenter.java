package starter.kit.app;

import android.os.Bundle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.BehaviorSubject;
import nucleus5.presenter.Factory;
import starter.kit.retrofit.RetrofitException;
import starter.kit.util.NetworkContract;
import starter.kit.util.RxUtils;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

public abstract class NetworkPresenter<T, ViewType extends NetworkContract.View> extends
    StarterPresenter<ViewType> implements NetworkContract.HudInterface {

  private static final int RESTARTABLE_ID = 2000;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableFirst(restartableId(), new Factory<Observable<T>>() {
      @Override public Observable<T> create() {
        return request().subscribeOn(io())
            .compose(RxUtils.hudTransformer(NetworkPresenter.this))
            .compose(RxLifecycleAndroid.bindFragment(BehaviorSubject.create()))
            .observeOn(mainThread());
      }
    }, new BiConsumer<ViewType, T>() {
      @Override public void accept(@NonNull ViewType viewType, @NonNull T item) throws Exception {
        //noinspection unchecked
        viewType.onSuccess(item);
      }
    }, new BiConsumer<ViewType, Throwable>() {
      @Override public void accept(@NonNull ViewType viewType, @NonNull Throwable throwable)
          throws Exception {
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
