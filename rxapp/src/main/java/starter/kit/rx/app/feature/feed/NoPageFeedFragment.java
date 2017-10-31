package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import nucleus5.factory.RequiresPresenter;
import starter.kit.app.StarterFragConfig;
import starter.kit.app.StarterRecyclerFragment;
import starter.kit.rx.app.R;
import starter.kit.rx.app.model.entity.Feed;

@RequiresPresenter(NoPageFeedPresenter.class) public class NoPageFeedFragment
    extends StarterRecyclerFragment<Feed, NoPageFeedPresenter> {

  public static NoPageFeedFragment create() {
    NoPageFeedFragment feedFragment = new NoPageFeedFragment();
    return feedFragment;
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    StarterFragConfig.Builder builder =
        new StarterFragConfig.Builder().addLoadingListItem(false) // 是否分页
            .pageSize(5)
            .bind(Feed.class, FeedsViewHolder.class)
            .recyclerViewDecor(new HorizontalDividerItemDecoration.Builder(getContext()).size(10)
                .colorResId(R.color.dividerColor)
                .build())
            .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);

    buildFragConfig(builder.build());
  }
}