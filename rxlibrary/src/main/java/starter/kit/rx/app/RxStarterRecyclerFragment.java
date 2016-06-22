package starter.kit.rx.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.ButterKnife;
import java.util.List;
import nucleus.presenter.Presenter;
import starter.kit.rx.R;
import support.ui.adapters.EasyRecyclerAdapter;

public abstract class RxStarterRecyclerFragment<P extends Presenter> extends RxStarterFragment<P> {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  public void viewHolderFactory(EasyRecyclerAdapter adapter) {
    // Left blank
  }

  public abstract void bindViewHolders(EasyRecyclerAdapter adapter);


  private EasyRecyclerAdapter mAdapter;

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mAdapter = new EasyRecyclerAdapter(getContext());
    viewHolderFactory(mAdapter);
    bindViewHolders(mAdapter);
  }

  @Override protected int getFragmentLayout() {
    return R.layout.starter_recycler_view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mSwipeRefreshLayout = ButterKnife.findById(view, R.id.swipeRefreshLayout);
    mRecyclerView = ButterKnife.findById(view, R.id.recyclerView);

    setupRecyclerView();
    setupSwipeRefreshLayout();
  }

  private void setupSwipeRefreshLayout() {

  }

  private void setupRecyclerView() {
    mRecyclerView.setLayoutManager(buildLayoutManager());
    mRecyclerView.setAdapter(mAdapter);
  }

  public RecyclerView.LayoutManager buildLayoutManager() {
    return new LinearLayoutManager(getContext());
  }

  public void appendAll(List<?> items) {
    mAdapter.appendAll(items);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
  }
}
