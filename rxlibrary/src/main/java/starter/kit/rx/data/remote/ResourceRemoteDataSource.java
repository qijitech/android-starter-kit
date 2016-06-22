package starter.kit.rx.data.remote;

import java.util.ArrayList;
import rx.Observable;
import starter.kit.model.Entity;
import starter.kit.rx.data.ResourceDataSource;

public class ResourceRemoteDataSource<E extends Entity> implements ResourceDataSource<E> {

  @Override public Observable<ArrayList<E>> requestData() {
    return null;
  }

  @Override public Observable<ArrayList<E>> requestNext() {
    return null;
  }
}
