package starter.kit.rx.app.feature.feed;

import android.os.Bundle;
import rx.Observable;
import starter.kit.app.PaginatorPresenter;
import starter.kit.model.dto.Paginator;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class PageFeedPresenter extends PaginatorPresenter<Paginator<Feed>> {

  private FeedService mFeedService;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();
  }

  @Override
  public Observable<Paginator<Feed>> request(String paginatorKey, int pageSize) {
    return mFeedService.paginator(paginatorKey, pageSize);
  }

  @Override public int restartableId() {
    return 1000;
  }
}
