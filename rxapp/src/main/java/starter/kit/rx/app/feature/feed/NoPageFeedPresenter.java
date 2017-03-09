package starter.kit.rx.app.feature.feed;

import android.os.Bundle;
import io.reactivex.Observable;
import starter.kit.pagination.LengthAwarePaginator;
import starter.kit.pagination.PaginatorPresenter;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class NoPageFeedPresenter extends PaginatorPresenter<LengthAwarePaginator<Feed>> {

  private FeedService mFeedService;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();
  }

  @Override public Observable<LengthAwarePaginator<Feed>> request(String firstPaginatorKey,
      String nextPaginatorKey, int perPage) {
    return mFeedService.fetchFeedsWithPage("100", perPage, "NoPageFeedPresenter");
  }

  @Override public int restartableId() {
    return 1002;
  }
}
