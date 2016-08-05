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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.functions.Action1;
import starter.kit.model.EmptyEntity;
import starter.kit.model.entity.Entity;
import starter.kit.retrofit.ErrorResponse;
import starter.kit.retrofit.RetrofitException;
import starter.kit.rx.R;
import starter.kit.rx.ResourcePresenter;
import starter.kit.rx.StarterFragConfig;
import starter.kit.rx.util.ProgressInterface;
import starter.kit.rx.util.RxIdentifier;
import starter.kit.rx.util.RxPager;
import starter.kit.rx.util.RxRequestKey;
import starter.kit.viewholder.StarterEmptyViewHolder;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public abstract class RxStarterRecyclerFragment
    extends RxStarterFragment<ResourcePresenter>
    implements com.paginate.Paginate.Callbacks,
    ProgressInterface,
    SwipeRefreshLayout.OnRefreshListener {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  private EasyRecyclerAdapter mAdapter;
  private Paginate mPaginate;
  private StarterFragConfig mFragConfig;

  private RxRequestKey mRequestKey;
  private EmptyEntity mEmptyEntity;

  public RxRequestKey getRequestKey() {
    return mRequestKey;
  }

  public StarterFragConfig getFragConfig() {
    return mFragConfig;
  }

  protected void buildFragConfig(StarterFragConfig fragConfig) {
    if (fragConfig == null) return;

    mFragConfig = fragConfig;

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
    mAdapter.bind(EmptyEntity.class, StarterEmptyViewHolder.class);
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

  public void notifyDataSetChanged(ArrayList<? extends Entity> items) {
    if (mEmptyEntity != null) {
      mEmptyEntity = null;
      mAdapter.clear();
    }

    if (mRequestKey.isFirstPage()) {
      mAdapter.clear();
    }
    mAdapter.appendAll(items);
    mRequestKey.received(items);

    hideProgress();

    if (mPaginate != null) {
      mPaginate.setHasMoreDataToLoad(false);
    }
  }

  @Override public void showProgress() {
    Observable.empty().observeOn(mainThread()).doOnTerminate(() -> {
      if (mRequestKey != null && mRequestKey.isFirstPage()) {
        mSwipeRefreshLayout.setRefreshing(true);
      } else if (mPaginate != null) {
        mPaginate.setHasMoreDataToLoad(true);
      }
    }).subscribe();
  }

  @Override public void hideProgress() {
    Observable.empty().observeOn(mainThread())
        .doOnTerminate(() -> mSwipeRefreshLayout.setRefreshing(false))
        .subscribe();
  }

  public void onError(RetrofitException throwable) {
    try {
      ErrorResponse errorResponse = throwable.getErrorBodyAs(ErrorResponse.class);
      System.out.println(errorResponse);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (mRequestKey.isFirstPage() && mAdapter.isEmpty()) {
      mAdapter.clear();
      mEmptyEntity = new EmptyEntity();
      mAdapter.add(mEmptyEntity);
    }

    if (mPaginate != null) {
      mPaginate.setHasMoreDataToLoad(false);
    }

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
    mRequestKey.reset();
    getPresenter().request();
  }

  // Paginate delegate
  @Override public void onLoadMore() {
    if (mPaginate != null && mRequestKey != null && mRequestKey.hasMoreData()) {
      mPaginate.setHasMoreDataToLoad(true);
      mRequestKey.next();
    }
  }

  @Override public boolean isLoading() {
    return mSwipeRefreshLayout.isRefreshing();
  }

  @Override public boolean hasLoadedAllItems() {
    return mRequestKey != null && !mRequestKey.hasMoreData();
  }
}
