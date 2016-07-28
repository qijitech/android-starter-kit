package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import nucleus.factory.RequiresPresenter;
import starter.kit.rx.StarterFragConfig;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.app.model.entity.Feed;

@RequiresPresenter(FeedPresenter.class)
public class FeedFragment extends RxStarterRecyclerFragment {

  public static FeedFragment create() {
    return new FeedFragment();
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    buildFragConfig(new StarterFragConfig.Builder<>()
            .pageSize(5)
            .bind(Feed.class, FeedsViewHolder.class)
            .recyclerViewDecor(new HorizontalDividerItemDecoration.Builder(getContext()).size(30)
                .colorResId(R.color.dividerColor)
                .build())
            .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
            .build());
  }
}