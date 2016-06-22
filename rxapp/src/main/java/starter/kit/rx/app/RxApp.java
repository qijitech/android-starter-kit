package starter.kit.rx.app;

import com.facebook.drawee.backends.pipeline.Fresco;
import starter.kit.retrofit.Network;

public class RxApp extends RxStarterApp {

  @Override public void onCreate() {
    super.onCreate();

    new Network.Builder()
        .accept(Profile.API_ACCEPT)
        .baseUrl(Profile.API_ENDPOINT)
        .build();

    Fresco.initialize(appContext());
  }
}
