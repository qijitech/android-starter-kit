package starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.functions.Action0;
import rx.functions.Action1;
import starter.kit.feature.rx.RxResourcePresenter;
import starter.kit.model.entity.Entity;
import starter.kit.rx.R;
import starter.kit.util.RxIdentifier;
import starter.kit.util.RxPager;
import starter.kit.util.RxRequestKey;
import starter.kit.util.RxUtils;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;

import static starter.kit.util.Utilities.isAdapterEmpty;
import static starter.kit.util.Utilities.isNotNull;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class StarterRecyclerFragment<P extends RxResourcePresenter>
    extends StarterNetworkFragment<P>
    implements com.paginate.Paginate.Callbacks,
    SwipeRefreshLayout.OnRefreshListener {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  private EasyRecyclerAdapter mAdapter;
  private Paginate mPaginate;

  private RxRequestKey mRequestKey;

  public RxRequestKey getRequestKey() {
    return mRequestKey;
  }

  public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  public RecyclerView getRecyclerView() {
    return mRecyclerView;
  }

  public void buildFragConfig(StarterFragConfig fragConfig) {
    if (fragConfig.isWithIdentifierRequest()) {
      mRequestKey = buildRxIdentifier(fragConfig);
    } else {
      mRequestKey = buildRxPager(fragConfig);
    }

    BaseEasyViewHolderFactory viewHolderFactory = fragConfig.getViewHolderFactory();
    if (viewHolderFactory != null) {
      mAdapter.viewHolderFactory(viewHolderFactory);
    }

    //noinspection unchecked
    HashMap<Class, Class<? extends EasyViewHolder>> boundViewHolders = fragConfig.getBoundViewHolders();
    if (!boundViewHolders.isEmpty()) {
      for (Map.Entry<Class, Class<? extends EasyViewHolder>> entry : boundViewHolders.entrySet()) {
        mAdapter.bind(entry.getKey(), entry.getValue());
      }
    }
    // bind empty value

    super.buildFragConfig(fragConfig);
  }

  private RxRequestKey buildRxIdentifier(StarterFragConfig fragConfig) {
    return new RxIdentifier(fragConfig.getPageSize(), new Action1<RxIdentifier>() {
      @Override public void call(RxIdentifier rxIdentifier) {
        getPresenter().requestNext(rxIdentifier);
      }
    });
  }

  private RxRequestKey buildRxPager(StarterFragConfig fragConfig) {
    return new RxPager(fragConfig.getStartPage(), fragConfig.getPageSize(), new Action1<RxPager>() {
      @Override public void call(RxPager pager) {
        getPresenter().requestNext(pager);
      }
    });
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mAdapter = new EasyRecyclerAdapter(getContext());
  }

  @Override protected int getFragmentLayout() {
    return R.layout.starter_recycler_view;
  }

  @Override public View targetView() {
    return mRecyclerView;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(getFragmentLayout(), container, false);
    mSwipeRefreshLayout = ButterKnife.findById(view, R.id.swipeRefreshLayout);
    mRecyclerView = ButterKnife.findById(view, R.id.supportUiContentRecyclerView);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupRecyclerView();
    setupPaginate();
    setupSwipeRefreshLayout();

    if (isNotNull(getFragConfig())) {
      List<Object> items = getFragConfig().getItems();
      if (isNotNull(items) && !items.isEmpty()) {
        mAdapter.addAll(items);
      }
    }
  }

  private void setupSwipeRefreshLayout() {
    if (isNotNull(getFragConfig())) {
      final StarterFragConfig fragConfig = getFragConfig();
      int[] colors = fragConfig.getColorSchemeColors();
      if (colors != null) {
        mSwipeRefreshLayout.setColorSchemeColors(colors);
      }
      boolean enabled = fragConfig.isEnabled();
      mSwipeRefreshLayout.setEnabled(enabled);
      if (enabled) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
      }
    }
  }

  private void setupPaginate() {
    if (isNotNull(getFragConfig())) {
      final StarterFragConfig fragConfig = getFragConfig();
      if (fragConfig.canAddLoadingListItem()) {
        mPaginate = Paginate.with(mRecyclerView, this)
            .setLoadingTriggerThreshold(fragConfig.getLoadingTriggerThreshold())
            .addLoadingListItem(true)
            .setLoadingListItemCreator(fragConfig.getLoadingListItemCreator())
            .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
              @Override public int getSpanSize() {
                return fragConfig.getSpanSizeLookup();
              }
            })
            .build();

        mPaginate.setHasMoreDataToLoad(false);
      }
    }
  }

  private void setupRecyclerView() {
    mRecyclerView.setAdapter(mAdapter);

    if (isNotNull(getFragConfig())) {
      final StarterFragConfig fragConfig = getFragConfig();
      RecyclerView.LayoutManager layoutManager = fragConfig.getLayoutManager();
      if (layoutManager != null) {
        mRecyclerView.setLayoutManager(layoutManager);
      } else {
        mRecyclerView.setLayoutManager(newLayoutManager());
      }

      RecyclerView.ItemDecoration decor = fragConfig.getDecor();
      if (decor != null) {
        mRecyclerView.addItemDecoration(decor);
      }

      RecyclerView.ItemAnimator animator = fragConfig.getAnimator();
      if (animator != null) {
        mRecyclerView.setItemAnimator(animator);
      }
    }
  }

  private RecyclerView.LayoutManager newLayoutManager() {
    return new LinearLayoutManager(getContext());
  }

  @Override public void showProgress() {
    if (isAdapterEmpty(mAdapter)) {
      super.showProgress();
    } else if (isNotNull(mRequestKey) && mRequestKey.isFirstPage()) {
      mSwipeRefreshLayout.setRefreshing(true);
    } else if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(true);
    }
  }

  @Override public void hideProgress() {
    RxUtils.empty(new Action0() {
      @Override public void call() {
        if (isNotNull(mSwipeRefreshLayout)) {
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
  }

  public void notifyDataSetChanged(ArrayList<? extends Entity> items) {
    if (mRequestKey.isFirstPage()) {
      mAdapter.clear();
    }

    mAdapter.appendAll(items);
    mRequestKey.received(items);

    if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(false);
    }

    if (isAdapterEmpty(mAdapter)) {
      showEmptyView();
    } else {
      showContentView();
    }
  }

  @Override public void onError(Throwable throwable) {
    if (mRequestKey.isFirstPage() && mAdapter.isEmpty()) {
      mAdapter.clear();
    }

    if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(false);
    }

    if (isAdapterEmpty(mAdapter)) {
      super.onError(throwable);
    }
  }

  @Override public void onResume() {
    super.onResume();
    if (isNotNull(mRequestKey) && !mRequestKey.requested()) {
      getPresenter().request();
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mAdapter = null;
    mPaginate = null;
  }

  @Override public void onRefresh() {
    mRequestKey.reset();
    getPresenter().request();
  }

  // Paginate delegate
  @Override public void onLoadMore() {
    if (isNotNull(mPaginate) && isNotNull(mRequestKey)
        && !isAdapterEmpty(mAdapter)
        && mRequestKey.hasMoreData()
        && !isLoading()) {
      mPaginate.setHasMoreDataToLoad(true);
      mRequestKey.next();
    }
  }

  @Override public boolean isLoading() {
    return isNotNull(mSwipeRefreshLayout)
        && isNotNull(mRequestKey)
        && (mSwipeRefreshLayout.isRefreshing() || mRequestKey.isLoading());
  }

  @Override public boolean hasLoadedAllItems() {
    return isNotNull(mRequestKey) && !mRequestKey.hasMoreData();
  }

  @Override public void onClick(View view) {
    onRefresh();
  }
}
