package starter.kit.rx;

import android.os.Bundle;
import java.util.ArrayList;
import rx.Observable;
import rx.subjects.PublishSubject;
import starter.kit.model.entity.Entity;
import starter.kit.retrofit.RetrofitException;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.util.RxPager;
import starter.kit.rx.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class ResourcePresenter extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<RxPager> pageRequests = PublishSubject.create();

  @SuppressWarnings("Unchecked") @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(RESTARTABLE_ID, () -> view().concatMap(
        fragment -> pageRequests.startWith(fragment.getRxPager()).concatMap((RxPager page) -> {
          Observable<ArrayList<? extends Entity>> observable =
              fragment.request(page.nextPage(), page.pageSize());
          return observable.subscribeOn(io())
              //.delay(5, TimeUnit.SECONDS)
              .compose(RxUtils.progressTransformer(fragment))
              .observeOn(mainThread());
        })), (feedFragment, feeds) -> feedFragment.notifyDataSetChanged(feeds),
        (feedFragment, throwable) -> {
          RetrofitException error = (RetrofitException) throwable;
          feedFragment.onError(error);
        });
  }

  public void request() {
    start(RESTARTABLE_ID);
  }

  public void requestNext(RxPager page) {
    pageRequests.onNext(page);
  }
}
