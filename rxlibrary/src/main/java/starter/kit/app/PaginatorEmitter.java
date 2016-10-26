package starter.kit.app;

import java.util.ArrayList;
import rx.functions.Action1;
import starter.kit.model.dto.Paginator;
import starter.kit.model.entity.Entity;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class PaginatorEmitter implements PaginatorInterface {

  private final StarterFragConfig mFragConfig;
  private int requested = NOT_REQUESTED;
  private Action1<PaginatorEmitter> onRequest;

  private boolean hasMoreData;
  private boolean isLoading;
  private String paginatorKey;
  private Paginator mPaginator;

  public PaginatorEmitter(StarterFragConfig fragConfig, Action1<PaginatorEmitter> onRequest) {
    this.mFragConfig = fragConfig;
    this.onRequest = onRequest;
    this.hasMoreData = true;
    this.isLoading = true;
  }

  public void received(Object data) {
    isLoading = false;

    if (data == null) {
      paginatorKey = null;
      hasMoreData = false;
      return;
    }

    if (data instanceof ArrayList) {
      //noinspection unchecked
      ArrayList<Entity> items = (ArrayList<Entity>) data;
      if (items.size() > 0) {
        final int itemCount = items.size();
        paginatorKey = items.get(itemCount - 1).identifier;
        hasMoreData = itemCount >= mFragConfig.getPageSize();
        requested += itemCount;
      } else {
        paginatorKey = null;
        hasMoreData = false;
      }
    } else if (data instanceof Paginator) {
      mPaginator = (Paginator) data;
      if (mPaginator.data() != null) {
        requested += mPaginator.data().size();
      }
      paginatorKey = String.valueOf(mPaginator.currentPage() + 1);
    }
  }

  @Override public void reset() {
    requested = NOT_REQUESTED;
    isLoading = false;
    paginatorKey = mFragConfig.isWithIdentifierRequest() ? null : String.valueOf(mFragConfig.getStartPage());
    hasMoreData = true;
  }

  @Override public boolean hasPages() {
    if (mPaginator != null) {
      return mPaginator.hasMorePages();
    }
    return hasMoreData;
  }

  @Override public boolean isFirstPage() {
    return requested == NOT_REQUESTED;
  }

  @Override public void request() {
    if (canRequest()) {
      isLoading = true;
      onRequest.call(this);
    }
  }

  @Override public int pageSize() {
    return mFragConfig.getPageSize();
  }

  @Override public boolean requested() {
    return requested != NOT_REQUESTED;
  }

  @Override public String paginatorKey() {
    return paginatorKey;
  }

  @Override public boolean canRequest() {
    return hasPages() && !isLoading;
  }

  @Override public boolean isLoading() {
    return isLoading;
  }
}
