package starter.kit.rx;

import android.os.Bundle;
import java.util.ArrayList;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.util.RxPager;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class ResourcePresenter extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<RxPager> pageRequests = PublishSubject.create();

  @SuppressWarnings("Unchecked")
  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(RESTARTABLE_ID, new Func0<Observable<ArrayList<?>>>() {
      @Override public Observable<ArrayList<?>> call() {
        return view().concatMap(new Func1<RxStarterRecyclerFragment, Observable<ArrayList<?>>>() {
          @Override public Observable<ArrayList<?>> call(RxStarterRecyclerFragment fragment) {
            return pageRequests.startWith(fragment.getRxPager())
                .concatMap(new Func1<RxPager, Observable<ArrayList<?>>>() {
                  @Override public Observable<ArrayList<?>> call(RxPager page) {
                    Observable<ArrayList<?>> observable = fragment.request(page.nextPage(), page.pageSize());
                    return observable.subscribeOn(io())
                        .doOnSubscribe(() -> fragment.showProgress())
                        .subscribeOn(mainThread())
                        .observeOn(mainThread());
                  }
                });
          }
        });
      }
    }, new Action2<RxStarterRecyclerFragment, ArrayList<?>>() {
      @Override public void call(RxStarterRecyclerFragment feedFragment, ArrayList<?> feeds) {
        feedFragment.notifyDataSetChanged(feeds);
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

  public void requestNext(RxPager page) {
    pageRequests.onNext(page);
  }
}
