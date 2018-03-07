package starter.kit.pagination;

import java.util.ArrayList;
import starter.kit.model.entity.Entity;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class AbstractPaginator<E extends Entity> implements PaginatorContract<E> {

  @Override public ArrayList<E> items() {
    return null;
  }

  @Override public E firstItem() {
    return null;
  }

  @Override public E lastItem() {
    return null;
  }

  @Override public Integer perPage() {
    return null;
  }

  @Override public Integer currentPage() {
    return null;
  }

  @Override public String firstPaginatorKey() {
    return null;
  }

  @Override public String nextPaginatorKey() {
    return null;
  }

  @Override public int total() {
    return 0;
  }

  @Override public int size() {
    return 0;
  }

  @Override public boolean hasMorePages() {
    return false;
  }

  @Override public boolean isEmpty() {
    return false;
  }
}
