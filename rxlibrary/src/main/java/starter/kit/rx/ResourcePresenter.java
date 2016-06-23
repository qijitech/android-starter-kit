package starter.kit.rx;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import starter.kit.rx.app.RxStarterRecyclerFragment;

public class ResourcePresenter extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<Integer> pageRequests = PublishSubject.create();

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(RESTARTABLE_ID, new Func0<Observable<ArrayList<?>>>() {
      @Override public Observable<ArrayList<?>> call() {
        return view().concatMap(new Func1<RxStarterRecyclerFragment, Observable<ArrayList<?>>>() {
          @Override public Observable<ArrayList<?>> call(RxStarterRecyclerFragment fragment) {
            return pageRequests.startWith(1)
                .concatMap(new Func1<Integer, Observable<ArrayList<?>>>() {
                  @Override public Observable<ArrayList<?>> call(Integer page) {
                    return fragment.request(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                  }
                });
          }
        });
      }
    }, new Action2<RxStarterRecyclerFragment, ArrayList<?>>() {
      @Override public void call(RxStarterRecyclerFragment feedFragment, ArrayList<?> feeds) {
        feedFragment.appendAll(feeds);
      }
    }, new Action2<RxStarterRecyclerFragment, Throwable>() {
      @Override public void call(RxStarterRecyclerFragment feedFragment, Throwable throwable) {
        feedFragment.onNetworkError(throwable);
      }
    });
  }

  public void request() {
    start(RESTARTABLE_ID);
  }

  public void requestNext(int page) {
    pageRequests.onNext(page);
  }
}
