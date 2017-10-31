package starter.kit.rx.app.feature.util;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import butterknife.BindView;
import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import starter.kit.app.StarterActivity;
import starter.kit.rx.app.R;

/**
 * Created by YuGang Yang on 06 29, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class RxPreferencesActivity extends StarterActivity {

  @BindView(R.id.foo_1) CheckBox foo1Checkbox;
  @BindView(R.id.foo_2) CheckBox foo2Checkbox;
  Preference<Boolean> fooPreference;
  CompositeDisposable subscriptions;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Views
    setContentView(R.layout.activity_rx_preferences);

    // Preferences
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    RxSharedPreferences rxPreferences = RxSharedPreferences.create(preferences);
    // foo
    fooPreference = rxPreferences.getBoolean("foo");
  }

  @Override protected void onResume() {
    super.onResume();

    subscriptions = new CompositeDisposable();
    bindPreference(foo1Checkbox, fooPreference);
    bindPreference(foo2Checkbox, fooPreference);
  }

  @Override protected void onPause() {
    super.onPause();
    subscriptions.dispose();
  }

  void bindPreference(CheckBox checkBox, Preference<Boolean> preference) {
    // Bind the preference to the checkbox.
    subscriptions.add(preference.asObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(RxCompoundButton.checked(checkBox)));
    // Bind the checkbox to the preference.
    subscriptions.add(
        RxCompoundButton.checkedChanges(checkBox).skip(1).subscribe(preference.asConsumer()));
  }
}
