package support.ui.content;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import support.ui.R;
import support.ui.utilities.BusProvider;

public class DefaultContentView extends FrameLayout
    implements SwipeRefreshLayout.OnRefreshListener {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  public DefaultContentView(Context context) {
    super(context);
    initialize(context);
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.support_ui_content_recycler_view, this, false);
    addView(view);
    mSwipeRefreshLayout = ButterKnife.findById(this, R.id.support_ui_content_swipe_refresh_layout);
    mSwipeRefreshLayout.setOnRefreshListener(this);
    mRecyclerView = ButterKnife.findById(this, R.id.support_ui_content_recycler_view);
  }

  @Override public void onRefresh() {
    BusProvider.getInstance().post(produceRefresh());
  }

  public RefreshEvent produceRefresh() {
    return new RefreshEvent();
  }
}
