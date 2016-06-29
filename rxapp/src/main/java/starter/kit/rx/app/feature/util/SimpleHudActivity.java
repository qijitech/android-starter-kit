package starter.kit.rx.app.feature.util;

import android.os.Bundle;
import android.view.View;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterActivity;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.FeedService;
import starter.kit.rx.util.HudInterface;
import starter.kit.rx.util.RxUtils;
import work.wanghao.simplehud.SimpleHUD;

/**
 * Created by YuGang Yang on 06 29, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class SimpleHudActivity extends RxStarterActivity {

  private FeedService mFeedService;
  Subscription subscription;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_hud);
    mFeedService = ApiService.createFeedService();
    SimpleHUD.backgroundHexColor = "#aa817EDF";
  }

  @OnClick(R.id.btn_simple_hud) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_simple_hud:
        doSimpleHud();
        break;
    }
  }

  private void doSimpleHud() {
    subscription = mFeedService.fetchFeeds(1, 20)
        .subscribeOn(Schedulers.io())
        .compose(RxUtils.hudTransformer((HudInterface) () ->
            RxUtils.showHud(this, "Loading...", () -> {
              RxUtils.unsubscribe(subscription);
              subscription = null;
            })))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(feeds -> {
          SimpleHUD.showInfoMessage(this, "成功");
        }, throwable -> {
          SimpleHUD.showInfoMessage(this, throwable.getLocalizedMessage());
        });
  }
}
