package starter.kit.rx.app.feature.main;

import android.os.Bundle;
import android.widget.FrameLayout;
import butterknife.Bind;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterActivity;
import starter.kit.rx.app.feature.feed.FeedFragment;

public class MainActivity extends RxStarterActivity {

  @Bind(R.id.fragmentContainer) FrameLayout frameLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragmentContainer, new FeedFragment())
          .commit();
    }
  }
}
