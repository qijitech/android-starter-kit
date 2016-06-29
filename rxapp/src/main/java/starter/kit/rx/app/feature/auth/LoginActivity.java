package starter.kit.rx.app.feature.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import nucleus.factory.RequiresPresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import starter.kit.retrofit.ErrorResponse;
import starter.kit.retrofit.RetrofitException;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterActivity;
import starter.kit.rx.app.model.entity.User;
import support.ui.app.SupportApp;
import work.wanghao.simplehud.SimpleHUD;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by YuGang Yang on 06 29, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
@RequiresPresenter(AuthPresenter.class)
public class LoginActivity extends RxStarterActivity<AuthPresenter> {

  @Bind(R.id.container_login_username) TextInputLayout mUsernameContainer;
  @Bind(R.id.container_login_password) TextInputLayout mPasswordContainer;
  @Bind(R.id.btn_login) Button mLoginBtn;
  EditText mUsernameEdit;
  EditText mPasswordEdit;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    setupViews();
    setupObservable();
  }

  private void setupObservable() {

    Observable<Boolean> usernameChangeObservable = RxTextView.textChanges(mUsernameEdit)
        .doOnNext(charSequence -> hideUsernameError())
        .debounce(400, TimeUnit.MILLISECONDS)
        .filter(charSequence -> !TextUtils.isEmpty(charSequence))
        .map(charSequence -> validateUsername(charSequence.toString()));

    Observable<Boolean> passwordChangeObservable = RxTextView.textChanges(mPasswordEdit)
        .doOnNext(charSequence -> hidePasswordError())
        .debounce(400, TimeUnit.MILLISECONDS)
        .filter(charSequence -> !TextUtils.isEmpty(charSequence))
        .map(charSequence -> validatePassword(charSequence.toString()));

    // Checks for validity of the username input field
    Subscription usernameSubscription = usernameChangeObservable
        .observeOn(mainThread()) // UI Thread
        .subscribe(validFields -> {
          if (validFields) {
            hideUsernameError();
          } else {
            showUsernameError();
          }
        });
    add(usernameSubscription);

    // Checks for validity of the password input field
    Subscription passwordSubscription = passwordChangeObservable
        .observeOn(mainThread()) // UI Thread
        .subscribe(validFields -> {
          if (validFields) {
            hidePasswordError();
          } else {
            showPasswordError();
          }
        });
    add(passwordSubscription);

    // Checks for validity of all input fields
    Subscription btnSubscription =
        Observable.combineLatest(usernameChangeObservable, passwordChangeObservable,
            (isUsernameValid, isPasswordValid) -> isUsernameValid && isPasswordValid)
            .observeOn(AndroidSchedulers.mainThread()) // UI Thread
            .subscribe(RxView.enabled(mLoginBtn));
    add(btnSubscription);

    //Observable.combineLatest(RxTextView.textChanges(mUsernameEdit).map(charSequence ->
    //      charSequence != null && charSequence.length() >= 6),
    //    RxTextView.textChanges(mPasswordEdit).map(charSequence ->
    //        charSequence != null && charSequence.length() >= 6),
    //    (username, password) -> username && password)
    //    .subscribe(RxView.enabled(mLoginBtn));

    RxView.clicks(mLoginBtn)
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe(aVoid -> doLogin());
  }

  private boolean validateUsername(String username) {
    return username != null && username.trim().length() >= 6;
  }

  private boolean validatePassword(String password) {
    return password != null && password.trim().length() >= 6;
  }

  private void enableError(TextInputLayout textInputLayout) {
    if (textInputLayout.getChildCount() == 2)
      textInputLayout.getChildAt(1).setVisibility(View.VISIBLE);
  }

  private void disableError(TextInputLayout textInputLayout) {
    if (textInputLayout.getChildCount() == 2)
      textInputLayout.getChildAt(1).setVisibility(View.GONE);
  }

  private void showUsernameError(){
    enableError(mUsernameContainer);
    //mUsernameContainer.setErrorEnabled(true);
    mUsernameContainer.setError(SupportApp.appResources().getString(R.string.login_username_error));
  }

  private void hideUsernameError(){
    disableError(mUsernameContainer);
    mUsernameContainer.setError(null);
  }

  private void showPasswordError(){
    enableError(mPasswordContainer);
    mPasswordContainer.setError(SupportApp.appResources().getString(R.string.login_username_error));
  }

  private void hidePasswordError(){
    disableError(mPasswordContainer);
    mPasswordContainer.setError(null);
  }

  private void setupViews() {
    mUsernameEdit = mUsernameContainer.getEditText();
    mPasswordEdit = mPasswordContainer.getEditText();
  }

  private void doLogin() {
    getPresenter().requestItem(mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString());
  }

  void onError(Throwable throwable) {
    RetrofitException retrofitException = (RetrofitException) throwable;
    try {
      ErrorResponse errorResponse = retrofitException.getErrorBodyAs(ErrorResponse.class);
      SimpleHUD.showErrorMessage(this, errorResponse.getMessage());
    } catch (IOException e) {
      SimpleHUD.showErrorMessage(this, throwable.getMessage());
    }
  }

  void onSuccess(User user) {
    SimpleHUD.showInfoMessage(this, "登录成功");
  }

}
