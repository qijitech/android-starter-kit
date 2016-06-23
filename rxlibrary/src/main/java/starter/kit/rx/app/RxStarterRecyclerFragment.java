package starter.kit.rx.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.paginate.Paginate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nucleus.factory.RequiresPresenter;
import rx.Observable;
import starter.kit.model.EmptyEntity;
import starter.kit.model.entity.Entity;
import starter.kit.rx.R;
import starter.kit.rx.ResourcePresenter;
import starter.kit.rx.StarterFragConfig;
import starter.kit.rx.util.RxPager;
import starter.kit.viewholder.StarterEmptyViewHolder;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;

@RequiresPresenter(ResourcePresenter.class)
public abstract class RxStarterRecyclerFragment<E extends Entity>
    extends RxStarterFragment<ResourcePresenter>
    implements com.paginate.Paginate.Callbacks,
    SwipeRefreshLayout.OnRefreshListener {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  private EasyRecyclerAdapter mAdapter;
  private Paginate mPaginate;
  private StarterFragConfig mFragConfig;

  private RxPager pager;

  private EmptyEntity mEmptyEntity;

  public abstract Observable<ArrayList<E>> request(int page, int pageSize);

  public RxPager getRxPager() {
    return pager;
  }

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
    // bind empty value
    mAdapter.bind(EmptyEntity.class, StarterEmptyViewHolder.class);
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mAdapter = new EasyRecyclerAdapter(getContext());

    if (bundle == null) {
      getPresenter().request();
    }
  }

  @Override protected int getFragmentLayout() {
    return R.layout.starter_recycler_view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mSwipeRefreshLayout = ButterKnife.findById(view, R.id.swipeRefreshLayout);
    mRecyclerView = ButterKnife.findById(view, R.id.recyclerView);

    setupRecyclerView();
    setupPaginate();
    setupSwipeRefreshLayout();
  }

  private void setupSwipeRefreshLayout() {
    if (mFragConfig != null) {
      int[] colors = mFragConfig.getColorSchemeColors();
      if (colors != null) {
        mSwipeRefreshLayout.setColorSchemeColors(colors);
      }
      boolean enabled = mFragConfig.isEnabled();
      mSwipeRefreshLayout.setEnabled(enabled);
      if (enabled) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
      }
    }
  }

  private void setupPaginate() {
    if (mFragConfig != null) {
      if (mFragConfig.canAddLoadingListItem()) {

        mPaginate = Paginate.with(mRecyclerView, this)
            .setLoadingTriggerThreshold(mFragConfig.getLoadingTriggerThreshold())
            .addLoadingListItem(true)
            .setLoadingListItemSpanSizeLookup(() -> mFragConfig.getSpanSizeLookup())
            .build();

        mPaginate.setHasMoreDataToLoad(false);
        pager = new RxPager(mFragConfig.getStartPage(), mFragConfig.getPageSize(), page -> {
          mPaginate.setHasMoreDataToLoad(true);
          getPresenter().requestNext(page);
        });

      } else {
        pager = new RxPager(mFragConfig.getStartPage(), mFragConfig.getPageSize(), null);
      }
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

  public void notifyDataSetChanged(List<?> items) {
    if (mEmptyEntity != null) {
      mEmptyEntity = null;
      mAdapter.clear();
    }

    if (pager.isFirstPage()) {
      mAdapter.clear();
    }
    mAdapter.appendAll(items);
    pager.received(items.size());
    mPaginate.setHasMoreDataToLoad(false);
    hideProgress();
  }

  public void showProgress() {
    mSwipeRefreshLayout.setRefreshing(true);
  }

  public void hideProgress() {
    mSwipeRefreshLayout.setRefreshing(false);
  }

  public void onNetworkError(Throwable throwable) {
    if (pager.isFirstPage() && mAdapter.isEmpty()) {
      mAdapter.clear();
      mEmptyEntity = new EmptyEntity();
      mAdapter.add(mEmptyEntity);
    }
    mPaginate.setHasMoreDataToLoad(false);
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
    mPaginate = null;
  }

  @Override public void onRefresh() {
    pager.reset();
    getPresenter().request();
  }

  // Paginate delegate
  @Override public void onLoadMore() {
    if (pager != null) {
      mPaginate.setHasMoreDataToLoad(false);
      pager.next();
    }
  }

  @Override public boolean isLoading() {
    return mSwipeRefreshLayout.isRefreshing();
  }

  @Override public boolean hasLoadedAllItems() {
    return pager != null && !pager.hasMorePage();
  }
}
