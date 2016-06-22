package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import nucleus.factory.RequiresPresenter;
import starter.kit.rx.StarterFragConfig;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.rx.util.RxPager;

@RequiresPresenter(FeedPresenter.class) public class FeedFragment
    extends RxStarterRecyclerFragment<FeedPresenter> {

  private RxPager pager;

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    buildFragConfig(new StarterFragConfig.Builder<>().viewHolderFactory(new FeedViewHolderFactory(getContext()))
        .bind(Feed.class, FeedsTextViewHolder.class)
        .recyclerViewDecor(new HorizontalDividerItemDecoration.Builder(getContext()).size(30)
            .colorResId(R.color.dividerColor)
            .build())
        .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
        .build());

    if (bundle == null) getPresenter().request();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    pager = new RxPager(10, page -> {
      //adapter.showProgress();
      getPresenter().requestNext(page);
    });
  }
}