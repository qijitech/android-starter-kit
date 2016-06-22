package starter.kit.rx.app.feature.feed;

import android.os.Bundle;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import starter.kit.rx.RxStarterPresenter;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class FeedPresenter extends RxStarterPresenter<FeedFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<Integer> pageRequests = PublishSubject.create();

  private FeedService mFeedService;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();

    restartableReplay(RESTARTABLE_ID,
        () -> pageRequests.startWith(1)
            .concatMap(page
                -> mFeedService.fetchFeeds(page, 30)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())),
        (activity, data) -> activity.appendAll(data),
        FeedFragment::onNetworkError);
  }

  void request() {
    start(RESTARTABLE_ID);
  }

  void requestNext(int page) {
    pageRequests.onNext(page);
  }
}
