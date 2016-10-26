package starter.kit.app;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public interface PaginatorInterface {

  int NOT_REQUESTED = -1;

  void received(Object data);

  /**
   * Determine if there is more items in the data store.
   *
   * @return has more
   */
  boolean hasPages();

  boolean isFirstPage();

  void reset();

  void request();

  int pageSize();

  String paginatorKey();

  boolean canRequest();

  boolean requested();

  boolean isLoading();
}
