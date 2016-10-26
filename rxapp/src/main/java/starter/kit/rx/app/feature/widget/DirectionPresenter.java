package starter.kit.rx.app.feature.widget;

import android.os.Bundle;
import rx.subjects.PublishSubject;
import starter.kit.app.StarterPresenter;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class DirectionPresenter extends StarterPresenter<DirectionActivity> {

  private static final int RESTARTABLE_ID = 1;

  private FeedService mFeedService;

  private PublishSubject<Integer> pageRequests = PublishSubject.create();

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();
  }

  void request() {
    start(RESTARTABLE_ID);
  }

  void requestNext(int page) {
    pageRequests.onNext(page);
  }
}
