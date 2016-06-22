package starter.kit.rx.data;

import java.util.ArrayList;
import rx.Observable;
import starter.kit.model.Entity;

import static starter.kit.util.Preconditions.checkNotNull;

public class ResourceRepository<E extends Entity> implements ResourceDataSource<E> {

  private final ResourceDataSource<E> mResourceRemoteDataSource;

  public ResourceRepository(ResourceDataSource<E> resourceRemoteDataSource) {
    mResourceRemoteDataSource = checkNotNull(resourceRemoteDataSource);
  }

  @Override public Observable<ArrayList<E>> requestData() {
    return mResourceRemoteDataSource.requestData();
  }

  @Override public Observable<ArrayList<E>> requestNext() {
    return mResourceRemoteDataSource.requestNext();
  }
}
