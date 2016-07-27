package starter.kit.rx;

import android.os.Bundle;
import java.util.ArrayList;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import starter.kit.model.entity.Entity;
import starter.kit.retrofit.RetrofitException;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.util.RxPager;
import starter.kit.rx.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public abstract class ResourcePresenter<T extends Entity> extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<RxPager> pageRequests = PublishSubject.create();

  @SuppressWarnings("Unchecked") @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(RESTARTABLE_ID, new Func0<Observable<ArrayList<T>>>() {
      @Override public Observable<ArrayList<T>> call() {
        return observableFactory();
      }
    }, new Action2<RxStarterRecyclerFragment, ArrayList<T>>() {
      @Override public void call(RxStarterRecyclerFragment fragment, ArrayList<T> feeds) {
        fragment.notifyDataSetChanged(feeds);
      }
    }, new Action2<RxStarterRecyclerFragment, Throwable>() {
      @Override public void call(RxStarterRecyclerFragment fragment, Throwable throwable) {
        RetrofitException error = (RetrofitException) throwable;
        fragment.onError(error);
      }
    });
  }

  private Observable<ArrayList<T>> observableFactory() {
    return view().concatMap(new Func1<RxStarterRecyclerFragment, Observable<ArrayList<T>>>() {
      @Override public Observable<ArrayList<T>> call(RxStarterRecyclerFragment fragment) {
        return pageRequests.startWith(fragment.getRxPager())
            .concatMap(new Func1<RxPager, Observable<? extends ArrayList<T>>>() {
              @Override public Observable<? extends ArrayList<T>> call(RxPager pager) {
                return request(pager.nextPage(), pager.pageSize()).subscribeOn(io())
                    .compose(RxUtils.progressTransformer(fragment))
                    .observeOn(mainThread());
              }
            });
      }
    });
  }

  public abstract Observable<ArrayList<T>> request(int page, int pageSize);

  public void request() {
    start(RESTARTABLE_ID);
  }

  public void requestNext(RxPager page) {
    pageRequests.onNext(page);
  }
}
