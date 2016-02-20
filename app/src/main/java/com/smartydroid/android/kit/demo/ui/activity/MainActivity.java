package com.smartydroid.android.kit.demo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.smartydroid.android.kit.demo.R;
import com.smartydroid.android.kit.demo.ui.fragment.AccountFragment;
import com.smartydroid.android.kit.demo.ui.fragment.FeedsFragment;
import com.smartydroid.android.starter.kit.app.StarterActivity;

public class MainActivity extends StarterActivity {

  // Tab identifier
  public static final String TAB_FEEDS = "tab_feed_identifier";
  public static final String TAB_ACCOUNT = "tab_account_identifier";

  @Bind(android.R.id.tabhost) FragmentTabHost mTabHost;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupTab();
  }

  private void setupTab() {
    mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

    mTabHost.addTab(createTabSpec(TAB_FEEDS, R.string.tab_home,
        R.drawable.selector_tab_feeds), FeedsFragment.class, null);
    mTabHost.addTab(createTabSpec(TAB_ACCOUNT, R.string.tab_account,
        R.drawable.selector_tab_account), AccountFragment.class, null);
  }

  private TabHost.TabSpec createTabSpec(String tag, int stringRes, int drawableResId) {
    TabHost.TabSpec spec = mTabHost.newTabSpec(tag);
    spec.setIndicator(createTabIndicator(stringRes, drawableResId));
    spec.setContent(new TabHost.TabContentFactory() {
      public View createTabContent(String tag) {
        return findViewById(android.R.id.tabcontent);
      }
    });
    return spec;
  }

  private View createTabIndicator(int res, int drawableResId) {
    LinearLayout tabIndicator = (LinearLayout) LayoutInflater.from(this)
        .inflate(R.layout.tab_indicator, mTabHost.getTabWidget(), false);

    ImageView icon = (ImageView) tabIndicator.findViewById(android.R.id.icon1);
    icon.setImageResource(drawableResId);

    TextView text = ButterKnife.findById(tabIndicator, android.R.id.text1);
    text.setText(res);

    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabIndicator.getLayoutParams();

    tabIndicator.setEnabled(true);
    params.weight = 1.0F;
    tabIndicator.setGravity(Gravity.CENTER);

    return tabIndicator;
  }

}

