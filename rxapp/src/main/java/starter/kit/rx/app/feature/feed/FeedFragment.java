package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.ArrayList;
import rx.Observable;
import starter.kit.rx.StarterFragConfig;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class FeedFragment extends RxStarterRecyclerFragment<Feed> {

  private FeedService mFeedService;

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mFeedService = ApiService.createFeedService();
    buildFragConfig(
        new StarterFragConfig.Builder<>().viewHolderFactory(new FeedViewHolderFactory(getContext()))
            .bind(Feed.class, FeedsTextViewHolder.class)
            .recyclerViewDecor(new HorizontalDividerItemDecoration.Builder(getContext()).size(30)
                .colorResId(R.color.dividerColor)
                .build())
            .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
            .build());
  }

  @Override public Observable<ArrayList<Feed>> request(int page, int pageSize) {
    return mFeedService.fetchFeeds(page, pageSize);
  }
}