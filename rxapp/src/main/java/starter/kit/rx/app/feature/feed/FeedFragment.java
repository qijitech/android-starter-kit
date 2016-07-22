package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.ArrayList;
import rx.Observable;
import rx.functions.Func2;
import starter.kit.rx.StarterFragConfig;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;

public class FeedFragment extends RxStarterRecyclerFragment<Feed> {

  private FeedService mFeedService;

  public static FeedFragment create() {
    return new FeedFragment();
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mFeedService = ApiService.createFeedService();


    buildFragConfig(new StarterFragConfig.Builder<>()
            .pageSize(5)
            .bind(Feed.class, FeedsViewHolder.class)
            .recyclerViewDecor(new HorizontalDividerItemDecoration.Builder(getContext()).size(30)
                .colorResId(R.color.dividerColor)
                .build())
            .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
            .requestFunc(new Func2<Integer, Integer, Observable<ArrayList<Feed>>>() {
              @Override public Observable<ArrayList<Feed>> call(Integer page, Integer pageSize) {
                return mFeedService.fetchFeeds(page, pageSize);
              }
            })
            .build());
  }
}