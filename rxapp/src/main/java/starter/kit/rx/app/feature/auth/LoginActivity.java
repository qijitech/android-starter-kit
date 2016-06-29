package starter.kit.rx.app.feature.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import nucleus.factory.RequiresPresenter;
import rx.Observable;
import starter.kit.retrofit.ErrorResponse;
import starter.kit.retrofit.RetrofitException;
import starter.kit.rx.app.R;
import starter.kit.rx.app.RxStarterActivity;
import starter.kit.rx.app.model.entity.User;
import work.wanghao.simplehud.SimpleHUD;

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
    Observable.combineLatest(RxTextView.textChanges(mUsernameEdit).map(charSequence ->
          charSequence != null && charSequence.length() >= 6),
        RxTextView.textChanges(mPasswordEdit).map(charSequence ->
            charSequence != null && charSequence.length() >= 6),
        (username, password) -> username && password)
        .subscribe(RxView.enabled(mLoginBtn));

    RxView.clicks(mLoginBtn)
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe(aVoid -> doLogin());
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
