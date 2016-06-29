package starter.kit.rx.app.feature.auth;

import android.os.Bundle;
import icepick.State;
import starter.kit.rx.RxStarterPresenter;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.AuthService;
import starter.kit.rx.util.HudInterface;
import starter.kit.rx.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * Created by YuGang Yang on 06 29, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class AuthPresenter extends RxStarterPresenter<LoginActivity> {

  public static final int AUTH_LOGIN_REQUEST = 1;
  private AuthService mAuthService;

  @State String username;
  @State String password;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mAuthService = ApiService.createAuthService();

    restartableLatestCache(AUTH_LOGIN_REQUEST,
        () -> view().concatMap(loginActivity -> mAuthService.login(username, password)
            .subscribeOn(io())
            .compose(RxUtils.hudTransformer((HudInterface) () -> RxUtils.showHud(loginActivity, "Login...", () -> stop(AUTH_LOGIN_REQUEST))))
            .observeOn(mainThread())),
        LoginActivity::onSuccess,
        LoginActivity::onError);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mAuthService = null;
  }

  void requestItem(String username, String password) {
    this.username = username;
    this.password = password;
    start(AUTH_LOGIN_REQUEST);
  }
}
