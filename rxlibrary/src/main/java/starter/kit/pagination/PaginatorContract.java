package starter.kit.pagination;

import java.util.ArrayList;
import starter.kit.model.entity.Entity;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public interface PaginatorContract<E extends Entity> {

  /**
   * Get all of the items being paginated.
   *
   * @return ArrayList<E>
   */
  ArrayList<E> items();

  /**
   * Get the "index" of the first item being paginated.
   *
   * @return E
   */
  E firstItem();

  /**
   * Get the "index" of the last item being paginated.
   *
   * @return E
   */
  E lastItem();

  /**
   * Determine how many items are being shown per page.
   *
   * @return int
   */
  int perPage();

  /**
   * Determine the current page being paginated.
   *
   * @return int
   */
  int currentPage();

  /**
   * Get the page number of the last available page key.
   *
   * @return String
   */
  String firstPaginatorKey();

  /**
   * Get the page number of the last available page key.
   *
   * @return String
   */
  String nextPaginatorKey();

  /**
   * Determine the total number of items in the data store.
   *
   * @return int
   */
  int total();

  /**
   * Current items size
   *
   * @return int
   */
  int size();

  /**
   * Determine if there are more items in the data source.
   *
   * @return boolean
   */
  boolean hasMorePages();

  /**
   * Determine if the list of items is empty or not.
   *
   * @return boolean
   */
  boolean isEmpty();
}
