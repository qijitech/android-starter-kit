package starter.kit.rx.app.feature.feed;

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
import starter.kit.rx.RxStarterPresenter;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class FeedPresenter extends RxStarterPresenter<FeedFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<Integer> pageRequests = PublishSubject.create();

  private FeedService mFeedService;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();

    restartableReplay(RESTARTABLE_ID, new Func0<Observable<ArrayList<Feed>>>() {
      @Override public Observable<ArrayList<Feed>> call() {
        return pageRequests.startWith(1)
            .concatMap(new Func1<Integer, Observable<ArrayList<Feed>>>() {
              @Override public Observable<ArrayList<Feed>> call(Integer page) {
                return mFeedService.fetchFeeds(page, 30)
                    .subscribeOn(Schedulers.io())
                    .delay(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
              }
            });
      }
    }, new Action2<FeedFragment, ArrayList<Feed>>() {
      @Override public void call(FeedFragment feedFragment, ArrayList<Feed> feeds) {
        feedFragment.appendAll(feeds);
      }
    }, new Action2<FeedFragment, Throwable>() {
      @Override public void call(FeedFragment feedFragment, Throwable throwable) {
        feedFragment.onNetworkError(throwable);
      }
    });
  }

  void request() {
    start(RESTARTABLE_ID);
  }

  void requestNext(int page) {
    pageRequests.onNext(page);
  }
}
