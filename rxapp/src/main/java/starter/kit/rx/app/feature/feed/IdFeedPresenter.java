package starter.kit.rx.app.feature.feed;

import android.os.Bundle;
import java.util.ArrayList;
import rx.Observable;
import starter.kit.app.PaginatorPresenter;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class IdFeedPresenter extends PaginatorPresenter<ArrayList<Feed>> {

  private FeedService mFeedService;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();
  }

  @Override
  public Observable<ArrayList<Feed>> request(String paginatorKey, int pageSize) {
    return mFeedService.fetchFeeds(paginatorKey, pageSize, "IdFeedPresenter");
  }

  @Override public int restartableId() {
    return 1001;
  }
}
