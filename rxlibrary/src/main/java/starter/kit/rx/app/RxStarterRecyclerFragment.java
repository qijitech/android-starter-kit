package starter.kit.rx.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nucleus.presenter.Presenter;
import starter.kit.rx.R;
import starter.kit.rx.StarterFragConfig;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;

public abstract class RxStarterRecyclerFragment<P extends Presenter> extends RxStarterFragment<P> {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  private EasyRecyclerAdapter mAdapter;

  private StarterFragConfig mFragConfig;

  protected void buildFragConfig(StarterFragConfig fragConfig) {
    if (fragConfig == null) return;

    mFragConfig = fragConfig;

    BaseEasyViewHolderFactory viewHolderFactory = fragConfig.getViewHolderFactory();
    if (viewHolderFactory != null) {
      mAdapter.viewHolderFactory(viewHolderFactory);
    }

    HashMap<Class, Class<? extends EasyViewHolder>> boundViewHolders =
        fragConfig.getBoundViewHolders();
    if (!boundViewHolders.isEmpty()) {
      for (Map.Entry<Class, Class<? extends EasyViewHolder>> entry : boundViewHolders.entrySet()) {
        mAdapter.bind(entry.getKey(), entry.getValue());
      }
    }
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mAdapter = new EasyRecyclerAdapter(getContext());
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
    if (mFragConfig != null) {
      int[] colors = mFragConfig.getColorSchemeColors();
      if (colors != null) {
        mSwipeRefreshLayout.setColorSchemeColors(colors);
      }
      mSwipeRefreshLayout.setEnabled(mFragConfig.isEnabled());
    }
  }

  private void setupRecyclerView() {
    mRecyclerView.setAdapter(mAdapter);

    if (mFragConfig != null) {
      RecyclerView.LayoutManager layoutManager = mFragConfig.getLayoutManager();
      if (layoutManager != null) {
        mRecyclerView.setLayoutManager(layoutManager);
      } else {
        mRecyclerView.setLayoutManager(newLayoutManager());
      }

      RecyclerView.ItemDecoration decor = mFragConfig.getDecor();
      if (decor != null) {
        mRecyclerView.addItemDecoration(decor);
      }

      RecyclerView.ItemAnimator animator = mFragConfig.getAnimator();
      if (animator != null) {
        mRecyclerView.setItemAnimator(animator);
      }
    }
  }

  private RecyclerView.LayoutManager newLayoutManager() {
    return new LinearLayoutManager(getContext());
  }

  public void appendAll(List<?> items) {
    mAdapter.appendAll(items);
    hideProgress();
  }

  public void showProgress() {
    mSwipeRefreshLayout.setRefreshing(true);
  }

  public void hideProgress() {
    mSwipeRefreshLayout.setRefreshing(false);
  }

  public void onNetworkError(Throwable throwable) {
    hideProgress();
    Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mAdapter = null;
    mFragConfig = null;
  }
}
