package starter.kit.rx.app;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.fasterxml.jackson.databind.ObjectMapper;
import starter.kit.app.StarterApp;
import starter.kit.model.entity.Account;
import starter.kit.retrofit.Network;
import starter.kit.rx.app.util.InitializeUtil;

public class RxApp extends StarterApp {

  @Override public void onCreate() {
    super.onCreate();

    new Network.Builder()
        .networkDebug(true)
        .accept(Profile.API_ACCEPT)
        .baseUrl(Profile.API_ENDPOINT)
        .build();

    Fresco.initialize(appContext());

    InitializeUtil.initialize();
  }

  @Override public Account provideAccount(String accountJson) {
    // TODO: 2016/11/10 Must Convert to Account Obj
    return null;
  }
}
