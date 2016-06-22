package starter.kit.rx.data;

import java.util.ArrayList;
import rx.Observable;
import starter.kit.model.Entity;

public interface ResourceDataSource<E extends Entity> {

  Observable<ArrayList<E>> requestData();

  Observable<ArrayList<E>> requestNext();
}
