package starter.kit.rx;

import java.util.ArrayList;
import rx.Observable;
import starter.kit.model.Entity;

public class StarterFragConfig<E extends Entity> {

  private Observable<ArrayList<E>> resourceObservable;

  public static class Builder<E extends Entity> {
    private Observable<ArrayList<E>> resourceObservable;

    public StarterFragConfig build() {
      StarterFragConfig config = new StarterFragConfig();
      config.resourceObservable = resourceObservable;
      return config;
    }

    public Builder observable(Observable<ArrayList<E>> observable) {
      resourceObservable = observable;
      return this;
    }
  }
}
