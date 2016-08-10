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
import starter.kit.rx.util.RxRequestKey;
import starter.kit.rx.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public abstract class ResourcePresenter<T extends Entity> extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 100;

  private PublishSubject<RxRequestKey> pageRequests = PublishSubject.create();

  @SuppressWarnings("Unchecked") @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(restartableId(), new Func0<Observable<ArrayList<T>>>() {
      @Override public Observable<ArrayList<T>> call() {
        return observableFactory();
      }
    }, new Action2<RxStarterRecyclerFragment, ArrayList<T>>() {
      @Override public void call(RxStarterRecyclerFragment fragment, ArrayList<T> items) {
        fragment.notifyDataSetChanged(items);
      }
    }, new Action2<RxStarterRecyclerFragment, Throwable>() {
      @Override public void call(RxStarterRecyclerFragment fragment, Throwable throwable) {
        fragment.onError(throwable);
      }
    });
  }

  private Observable<ArrayList<T>> observableFactory() {
    return view().concatMap(new Func1<RxStarterRecyclerFragment, Observable<ArrayList<T>>>() {
      @Override public Observable<ArrayList<T>> call(RxStarterRecyclerFragment fragment) {
        return pageRequests.startWith(fragment.getRequestKey())
            .concatMap(new Func1<RxRequestKey, Observable<? extends ArrayList<T>>>() {
              @Override public Observable<? extends ArrayList<T>> call(RxRequestKey requestKey) {
                return request(requestKey.previousKey(), requestKey.nextKey(), requestKey.pageSize()).subscribeOn(io())
                    .compose(RxUtils.progressTransformer(fragment))
                    .observeOn(mainThread());
              }
            });
      }
    });
  }

  public int restartableId() {
    return RESTARTABLE_ID;
  }

  public abstract Observable<ArrayList<T>> request(String previousKey, String nextKey, int pageSize);

  public void request() {
    start(restartableId());
  }

  public void requestNext(RxRequestKey page) {
    pageRequests.onNext(page);
  }
}
