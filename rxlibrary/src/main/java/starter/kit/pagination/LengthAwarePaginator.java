package starter.kit.pagination;

import java.util.ArrayList;
import starter.kit.model.entity.Entity;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class LengthAwarePaginator<T extends Entity> extends ArrayList<T>
    implements PaginatorContract<T> {

  @Override public ArrayList<T> items() {
    return this;
  }

  @Override public T firstItem() {
    return null;
  }

  @Override public T lastItem() {
    return isEmpty() ? null : get(size() - 1);
  }

  @Override public Integer perPage() {
    return 0;
  }

  @Override public Integer currentPage() {
    return 0;
  }

  @Override public String firstPaginatorKey() {
    return null;
  }

  @Override public String nextPaginatorKey() {
    return null;
  }

  @Override public int total() {
    return super.size();
  }

  @Override public boolean hasMorePages() {
    return false;
  }

  @Override public boolean isEmpty() {
    return super.isEmpty();
  }
}
