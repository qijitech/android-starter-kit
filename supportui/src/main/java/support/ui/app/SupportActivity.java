package support.ui.app;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;

public class SupportActivity extends AppCompatActivity {

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }
}
