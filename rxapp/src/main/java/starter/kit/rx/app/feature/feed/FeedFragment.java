package starter.kit.rx.app.feature.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import nucleus.factory.RequiresPresenter;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.app.model.entity.Feed;
import starter.kit.util.RxPager;
import support.ui.adapters.EasyRecyclerAdapter;

@RequiresPresenter(FeedPresenter.class) public class FeedFragment
    extends RxStarterRecyclerFragment<FeedPresenter> {

  private RxPager pager;

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if (bundle == null)
      getPresenter().request();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    pager = new RxPager(10, page -> {
      //adapter.showProgress();
      getPresenter().requestNext(page);
    });
  }

  @Override public void bindViewHolders(EasyRecyclerAdapter adapter) {
    adapter.bind(Feed.class, FeedsTextViewHolder.class);
  }

  @Override public void viewHolderFactory(EasyRecyclerAdapter adapter) {
    adapter.viewHolderFactory(new FeedViewHolderFactory(getContext()));
  }

  void onNetworkError(Throwable throwable) {
    Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
  }
}
