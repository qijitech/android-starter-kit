package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import nucleus.factory.RequiresPresenter;
import starter.kit.feature.StarterFragConfig;
import starter.kit.rx.app.R;
import starter.kit.feature.rx.RxStarterRecyclerFragment;
import starter.kit.rx.app.model.entity.Feed;

@RequiresPresenter(FeedPresenter.class)
public class FeedFragment extends RxStarterRecyclerFragment {

  private boolean addLoadingListItem;
  private boolean withIdentifierRequest;
  public int position;

  public static FeedFragment create(int position, boolean addLoadingListItem, boolean withIdentifierRequest) {
    FeedFragment feedFragment = new FeedFragment();
    feedFragment.position = position;
    feedFragment.addLoadingListItem = addLoadingListItem;
    feedFragment.withIdentifierRequest = withIdentifierRequest;
    return feedFragment;
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    StarterFragConfig.Builder builder = new StarterFragConfig.Builder<>()
        .addLoadingListItem(addLoadingListItem) // 是否分页
        .withIdentifierRequest(withIdentifierRequest)
        .pageSize(5)
        .bind(Feed.class, FeedsViewHolder.class)
        .recyclerViewDecor(new HorizontalDividerItemDecoration
            .Builder(getContext()).size(10)
            .colorResId(R.color.dividerColor)
            .build())
        .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);

    buildFragConfig(builder.build());
  }

  public boolean withIdentifierRequest() {
    return  withIdentifierRequest;
  }
}