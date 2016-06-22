package starter.kit.rx;

import android.os.Bundle;
import rx.subjects.PublishSubject;
import starter.kit.rx.app.RxStarterRecyclerFragment;

public class ResourcePresenter extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<Integer> pageRequests = PublishSubject.create();

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
  }

  void request() {
    start(RESTARTABLE_ID);
  }

  void requestNext(int page) {
    pageRequests.onNext(page);
  }
}
