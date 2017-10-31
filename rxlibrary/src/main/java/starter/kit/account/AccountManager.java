package starter.kit.account;

import android.content.Context;
import android.text.TextUtils;
import starter.kit.app.StarterApp;
import starter.kit.model.entity.Account;

/**
 * 用户登录账户信息管理类
 */
public enum AccountManager {

  INSTANCE;

  private final AuthPreferences authPreferences;
  private Account mCurrentAccount;
  private Context mContext;

  AccountManager() {
    mContext = StarterApp.appContext();
    authPreferences = new AuthPreferences(mContext);
  }

  public boolean isLogin() {
    return authPreferences.isLogin();
  }

  /**
   * 退出登录
   */
  public void logout() {
    mCurrentAccount = null;
    authPreferences.clear();
  }

  /**
   * 保存登录信息
   *
   * @param account 登录参数
   */
  public void storeAccount(Account account) {
    mCurrentAccount = account;
    authPreferences.setToken(account.token());
    authPreferences.setUser(account.name());
    authPreferences.setUserData(account.toJson());
  }

  /**
   * @return 返回登录token
   */
  public String token() {
    return authPreferences.getToken();
  }

  /**
   * @return 返回登录用户名
   */
  public String user() {
    return authPreferences.getUser();
  }

  /**
   * @param <T> 登录实体类类型
   * @return 获取当前登录的账户信息
   */
  @SuppressWarnings("unchecked") public <T extends Account> T getCurrentAccount() {
    if (mCurrentAccount == null) {
      String accountJson = authPreferences.getUserData();
      if (!TextUtils.isEmpty(accountJson) && mContext instanceof AccountProvider) {
        mCurrentAccount = ((AccountProvider) mContext).provideAccount(accountJson);
      }
    }
    return (T) mCurrentAccount;
  }
}
