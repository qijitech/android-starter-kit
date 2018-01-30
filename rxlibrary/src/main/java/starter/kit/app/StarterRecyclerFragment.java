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
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action0;
import rx.functions.Action1;
import starter.kit.model.entity.Entity;
import starter.kit.pagination.PaginatorContract;
import starter.kit.pagination.PaginatorEmitter;
import starter.kit.pagination.PaginatorPresenter;
import starter.kit.rx.R;
import starter.kit.util.Lists;
import starter.kit.util.RxUtils;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;

import static starter.kit.util.Utilities.isAdapterEmpty;
import static starter.kit.util.Utilities.isNotNull;
import static starter.kit.util.Utilities.isNull;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class StarterRecyclerFragment<E extends Entity, PC extends PaginatorPresenter>
    extends StarterNetworkFragment<PaginatorContract<E>, PC>
    implements com.paginate.Paginate.Callbacks,
    SwipeRefreshLayout.OnRefreshListener {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  private EasyRecyclerAdapter mAdapter;
  private Paginate mPaginate;

  private PaginatorEmitter<E> mPaginatorEmitter;

  public PaginatorEmitter getPaginatorEmitter() {
    return mPaginatorEmitter;
  }

  public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  public RecyclerView getRecyclerView() {
    return mRecyclerView;
  }

  public void buildFragConfig(StarterFragConfig fragConfig) {
    mPaginatorEmitter = new PaginatorEmitter<>(fragConfig, new Action1<PaginatorEmitter<E>>() {
      @Override public void call(PaginatorEmitter paginatorEmitter) {
        getPresenter().requestNext(paginatorEmitter);
      }
    });

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

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mAdapter = new EasyRecyclerAdapter(getContext());
  }

  @Override protected int getFragmentLayout() {
    return R.layout.starter_recycler_view;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(getFragmentLayout(), container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mSwipeRefreshLayout = ButterKnife.findById(view, R.id.swipeRefreshLayout);
    mRecyclerView = ButterKnife.findById(view, R.id.supportUiContentRecyclerView);

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
    if (!isNotNull(mPaginatorEmitter) || !getFragConfig().shouldDisplayLoadingView()) {
      return;
    }
    mPaginatorEmitter.setLoading(true);
    if (isAdapterEmpty(mAdapter)) {
      super.showProgress();
    } else if (isNotNull(mPaginatorEmitter) && mPaginatorEmitter.isFirstPage()) {
      mSwipeRefreshLayout.setRefreshing(true);
    } else if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(true);
    }
  }

  @Override public void hideProgress() {
    if (isNotNull(mPaginatorEmitter)) {
      mPaginatorEmitter.setLoading(false);
    }
    RxUtils.empty(new Action0() {
      @Override public void call() {
        if (isNotNull(mSwipeRefreshLayout)) {
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
  }

  @Override public void onSuccess(PaginatorContract<E> paginatorContract) {
    if (isNull(mPaginatorEmitter)) {
      return;
    }

    ArrayList<? extends Entity> items = paginatorContract !=null ? paginatorContract.items() : Lists.newArrayList();
    if (mPaginatorEmitter.isFirstPage()) {
      mAdapter.clear();
    }

    if (items == null) { // handle null
      items = Lists.newArrayList();
    }

    mAdapter.appendAll(items);
    mPaginatorEmitter.received(paginatorContract);

    if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(false);
    }

    if (getContentPresenter() != null) {
      if (isAdapterEmpty(mAdapter) && getFragConfig().shouldDisplayEmptyView()) {
        getContentPresenter().displayEmptyView();
      } else {
        getContentPresenter().displayContentView();
      }
    }
  }

  @Override public void onError(Throwable throwable) {
    super.onError(throwable);

    if (isNull(mPaginatorEmitter)) {
      return;
    }

    if (!(throwable instanceof MissingBackpressureException)) { // 防止重复调用
      // error handle
      mPaginatorEmitter.received(null);
    }

    if (mPaginatorEmitter.isFirstPage() && mAdapter.isEmpty()) {
      mAdapter.clear();
    }

    if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(false);
    }

    if (isAdapterEmpty(mAdapter)) {
      getContentPresenter().displayErrorView();
    } else {
      getContentPresenter().displayContentView();
    }
  }

  @Override public void onResume() {
    super.onResume();
    if (isNotNull(mPaginatorEmitter) && !mPaginatorEmitter.requested()) {
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
    mPaginatorEmitter = null;
  }

  @Override public void onRefresh() {
    setErrorResponse(null);
    if (isNotNull(mPaginatorEmitter) && !mPaginatorEmitter.isLoading()) {
      mPaginatorEmitter.reset();
      mPaginatorEmitter.setLoading(true);
      getPresenter().request();
    } else {
      mSwipeRefreshLayout.setRefreshing(true);
    }
  }

  // Paginate delegate
  @Override public void onLoadMore() {
    if (isNotNull(mPaginate) && isNotNull(mPaginatorEmitter)
        && !isAdapterEmpty(mAdapter)
        && mPaginatorEmitter.canRequest()
        && !isLoading()) {
      mPaginate.setHasMoreDataToLoad(true);
      mPaginatorEmitter.request();
    }
  }

  @Override public boolean isLoading() {
    return isNotNull(mSwipeRefreshLayout)
        && isNotNull(mPaginatorEmitter)
        && (mSwipeRefreshLayout.isRefreshing() || mPaginatorEmitter.isLoading());
  }

  @Override public boolean hasLoadedAllItems() {
    return isNotNull(mPaginatorEmitter) && !mPaginatorEmitter.hasMorePages();
  }

  @Override public View provideContentView() {
    return mSwipeRefreshLayout;
  }

  @Override public void onEmptyViewClick(View view) {
    onRefresh();
  }

  @Override public void onErrorViewClick(View view) {
    onRefresh();
  }
}
