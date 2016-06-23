package starter.kit.rx.util;

import rx.functions.Action1;

public class RxPager {

  public static final int NOT_REQUESTED = -1;

  private final int startPage;
  private int nextPage;
  private final int pageSize;
  private int size = 0;
  private int requested = NOT_REQUESTED;
  private Action1<RxPager> onRequest;

  public RxPager(int startPage, int pageSize, Action1<RxPager> onRequest) {
    this.startPage = startPage;
    this.pageSize = pageSize;
    this.onRequest = onRequest;
  }

  public void next() {
    if (hasMorePage() && requested != size) {
      requested = size;
      onRequest.call(this);
    }
  }

  public boolean hasMorePage() {
    return size % pageSize == 0;
  }

  public void received(int itemCount) {
    size += itemCount;
    if (hasMorePage()) {
      nextPage = size / pageSize + startPage;
    }
  }

  public void reset() {
    size = 0;
    nextPage = startPage;
    requested = NOT_REQUESTED;
  }

  public int nextPage() {
    return nextPage;
  }

  public boolean isFirstPage() {
    return nextPage == startPage;
  }

  public int startPage() {
    return startPage;
  }

  public int pageSize() {
    return pageSize;
  }
}
