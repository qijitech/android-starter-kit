package starter.kit.pagination;

import starter.kit.model.entity.Entity;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public interface Emitter<E extends Entity> {

  boolean isFirstPage();

  void received(PaginatorContract<E> paginatorContract);

  void reset();

  void request();

  boolean canRequest();

  boolean requested();

  boolean isLoading();

  void setLoading(boolean isLoading);
}
