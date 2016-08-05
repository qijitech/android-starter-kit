package starter.kit.rx.app.feature.feed;

import android.os.Bundle;
import java.util.ArrayList;
import rx.Observable;
import starter.kit.rx.ResourcePresenter;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class FeedPresenter extends ResourcePresenter<Feed> {

  private FeedService mFeedService;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mFeedService = ApiService.createFeedService();
  }

  @Override
  public Observable<ArrayList<Feed>> request(String previousKey, String nextKey, int pageSize) {
    final FeedFragment feedFragment = (FeedFragment) getView();
    if (feedFragment != null && !feedFragment.withIdentifierRequest()) {
      return mFeedService.fetchFeedsWithPage(nextKey, pageSize);
    }
    return mFeedService.fetchFeeds(previousKey, nextKey, pageSize);
  }

  @Override public int restartableId() {
    final FeedFragment feedFragment = (FeedFragment) getView();
    if (feedFragment != null) {
      return feedFragment.position + 1000;
    }
    return super.restartableId();
  }
}
