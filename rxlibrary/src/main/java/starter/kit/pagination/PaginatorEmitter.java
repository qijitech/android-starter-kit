package starter.kit.pagination;

import java.util.ArrayList;
import rx.functions.Action1;
import starter.kit.app.StarterFragConfig;
import starter.kit.model.entity.Entity;
import starter.kit.util.Lists;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class PaginatorEmitter<E extends Entity> implements Emitter<E>, PaginatorContract<E> {

  private final StarterFragConfig mFragConfig;
  private Action1<PaginatorEmitter<E>> onRequest;

  private boolean hasMoreData;
  private boolean isLoading;
  private PaginatorContract mPaginatorContract;

  private int currentPage;
  private String firstPaginatorKey;
  private String nextPaginatorKey;

  private ArrayList<E> requestedItems = Lists.newArrayList();

  public PaginatorEmitter(StarterFragConfig fragConfig, Action1<PaginatorEmitter<E>> onRequest) {
    this.mFragConfig = fragConfig;
    this.currentPage = fragConfig.getStartPage();

    resetPaginatorKey();

    this.onRequest = onRequest;
    this.hasMoreData = true;
    this.isLoading = true;
  }

  private void resetPaginatorKey() {
    if (!mFragConfig.withKeyRequest()) {
      firstPaginatorKey = String.valueOf(currentPage);
      nextPaginatorKey = String.valueOf(currentPage);
    } else {
      firstPaginatorKey = null;
      nextPaginatorKey = null;
    }
  }

  @Override public void received(PaginatorContract<E> paginatorContract) {
    isLoading = false;
    mPaginatorContract = paginatorContract;
    if (paginatorContract == null) {
      resetPaginatorKey();
      hasMoreData = false;
      return;
    }

    if (paginatorContract.isEmpty()) {
      resetPaginatorKey();
      hasMoreData = false;
      return;
    }

    requestedItems.addAll(items());

    hasMoreData = hasMorePages() || paginatorContract.size() >= perPage();

    if (mFragConfig.withKeyRequest()) {
      firstPaginatorKey = requestedItems.get(0).paginatorKey();
      nextPaginatorKey = lastItem().paginatorKey();
    } else {
      firstPaginatorKey = String.valueOf(mFragConfig.getStartPage());
      nextPaginatorKey = String.valueOf(++currentPage);
    }
  }

  @Override public void reset() {
    requestedItems.clear();
    currentPage = mFragConfig.getStartPage();
    resetPaginatorKey();
    isLoading = false;
    hasMoreData = true;
  }

  @Override public boolean isFirstPage() {
    return requestedItems.isEmpty();
  }

  @Override public void request() {
    if (canRequest()) {
      isLoading = true;
      onRequest.call(this);
    }
  }

  @Override public boolean requested() {
    return !requestedItems.isEmpty();
  }

  @Override public boolean canRequest() {
    return hasMorePages() && !isLoading;
  }

  @Override public boolean isLoading() {
    return isLoading;
  }

  @Override public ArrayList<E> items() {
    //noinspection unchecked
    return mPaginatorContract.items();
  }

  @Override public E firstItem() {
    //noinspection unchecked
    return (E) mPaginatorContract.firstItem();
  }

  @Override public E lastItem() {
    //noinspection unchecked
    return (E) mPaginatorContract.lastItem();
  }

  @Override public int perPage() {
    return mFragConfig.getPageSize();
  }

  @Override public int currentPage() {
    return currentPage;
  }

  @Override public String firstPaginatorKey() {
    return firstPaginatorKey;
  }

  @Override public String nextPaginatorKey() {
    return nextPaginatorKey;
  }

  @Override public int total() {
    return mPaginatorContract != null ? mPaginatorContract.total() : 0;
  }

  @Override public int size() {
    return mPaginatorContract != null ? mPaginatorContract.size() : 0;
  }

  @Override public boolean hasMorePages() {
    if (mPaginatorContract != null) {
      return mPaginatorContract.hasMorePages() || hasMoreData;
    }
    return hasMoreData;
  }

  @Override public boolean isEmpty() {
    return mPaginatorContract != null && mPaginatorContract.isEmpty();
  }
}
