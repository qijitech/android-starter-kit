package starter.kit.rx.app.feature.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterActivity;

public class MainActivity extends RxStarterActivity {

  //save our header or result
  private AccountHeader headerResult = null;
  private Drawer result = null;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Handle Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //set the back arrow in the toolbar
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(R.string.app_name);

    // Create a few sample profile
    // NOTE you have to define the loader logic too. See the CustomApplication for more details
    final IProfile profile = new ProfileDrawerItem().withName("Smartydroid")
        .withEmail("smartydroid@gmail.com")
        .withIcon("https://avatars2.githubusercontent.com/u/13810934?v=3&s=460");

    // Create the AccountHeader
    headerResult = new AccountHeaderBuilder().withActivity(this)
        .withHeaderBackground(R.drawable.header)
        .addProfiles(profile)
        .withSavedInstance(savedInstanceState)
        .build();

    //Create the drawer
    result = new DrawerBuilder().withActivity(this)
        .withToolbar(toolbar)
        .withHasStableIds(true)
        .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
        .addDrawerItems(new PrimaryDrawerItem().withName("Feeds").withIcon(FontAwesome.Icon.faw_android))
        .addStickyDrawerItems(
            new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(10),
            new PrimaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github)
        )
        .withSavedInstance(savedInstanceState)
        .withShowDrawerOnFirstLaunch(true)
        .build();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    //add the values which need to be saved from the drawer to the bundle
    outState = result.saveInstanceState(outState);
    //add the values which need to be saved from the accountHeader to the bundle
    outState = headerResult.saveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override public void onBackPressed() {
    //handle the back press :D close the drawer first and if the drawer is closed close the activity
    if (result != null && result.isDrawerOpen()) {
      result.closeDrawer();
    } else {
      super.onBackPressed();
    }
  }
}
