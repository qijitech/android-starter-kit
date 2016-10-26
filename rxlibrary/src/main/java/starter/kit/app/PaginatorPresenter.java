package starter.kit.app;

import android.os.Bundle;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import starter.kit.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public abstract class PaginatorPresenter<T> extends
    StarterPresenter<StarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 100;

  private PublishSubject<PaginatorEmitter> mRequests = PublishSubject.create();

  @SuppressWarnings("Unchecked") @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(restartableId(), new Func0<Observable<T>>() {
      @Override public Observable<T> call() {
        return observableFactory();
      }
    }, new Action2<StarterRecyclerFragment, T>() {
      @Override public void call(StarterRecyclerFragment fragment, T items) {
        fragment.onSuccess(items);
      }
    }, new Action2<StarterRecyclerFragment, Throwable>() {
      @Override public void call(StarterRecyclerFragment fragment, Throwable throwable) {
        fragment.onError(throwable);
      }
    });
  }

  private Observable<T> observableFactory() {
    return view().concatMap(new Func1<StarterRecyclerFragment, Observable<T>>() {
      @Override public Observable<T> call(StarterRecyclerFragment fragment) {
        return mRequests.startWith(fragment.getPaginatorEmitter())
            .concatMap(new Func1<PaginatorEmitter, Observable<? extends T>>() {
              @Override public Observable<? extends T> call(PaginatorEmitter requestKey) {
                BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
                return request(requestKey.paginatorKey(), requestKey.pageSize()).subscribeOn(io())
                    .compose(RxUtils.progressTransformer(fragment))
                    .compose(RxLifecycle.bindFragment(lifecycle))
                    .observeOn(mainThread());
              }
            });
      }
    });
  }

  public int restartableId() {
    return RESTARTABLE_ID;
  }

  public abstract Observable<T> request(String paginatorKey, int pageSize);

  public void request() {
    start(restartableId());
  }

  public void requestNext(PaginatorEmitter paginator) {
    mRequests.onNext(paginator);
  }
}
