/**
 * Created by YuGang Yang on October 29, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.starter.kit.account;

import android.content.Context;
import android.text.TextUtils;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import java.util.ArrayList;
import me.alexrs.prefs.lib.Prefs;

/**
 * @author YuGang Yang <smartydroid@gmail.com>
 * @package com.smartydroid.android.starter.kit.account
 */
public class AccountManager implements AccountProvider {

  private static final String USER_STORAGE = "user_preference";
  private static final String USER_JSON_KEY = "user_json";

  private ArrayList<CurrentAccountObserver> mObservers;
  private Account mCurrentAccount;

  public interface CurrentAccountObserver {
    public void notifyChange();
  }

  public static void registerObserver(CurrentAccountObserver observer) {
    getObservers().add(observer);
  }

  public static void unregisterObserver(CurrentAccountObserver observer) {
    getObservers().remove(observer);
  }

  private static ArrayList<CurrentAccountObserver> getObservers() {
    if (getInstance().mObservers == null) {
      getInstance().mObservers = new ArrayList<>();
    }
    return getInstance().mObservers;
  }

  public static void notifyDataChanged() {
    if (getObservers() == null) {
      return;
    }
    for (CurrentAccountObserver observer : getObservers()) {
      observer.notifyChange();
    }
  }

  public static boolean isSameAccount(Account account) {
    return account != null
        && getCurrentAccount() != null
        && getCurrentAccount().key() == account.key();
  }

  public static Account getCurrentAccount() {
    return getInstance().mCurrentAccount;
  }

  public static boolean isLoggedIn() {
    return getCurrentAccount() != null;
  }

  public static void setCurrentAccount(Account account) {
    if (getInstance().mCurrentAccount == account) {
      return;
    }
    getInstance().mCurrentAccount = account;
  }

  public static void store() {
    getInstance().saveUserData();
  }

  /**
   * 保存用户
   */
  public static void store(Account account) {
    setCurrentAccount(account);
    getInstance().saveUserData();
  }

  public static void logout() {
    getInstance().clear();
    notifyDataChanged();
  }

  private AccountManager() {
    loadUserDataFromStorage();
  }

  private void loadUserDataFromStorage() {
    String userJson = prefs().getString(USER_JSON_KEY, null);
    if (!TextUtils.isEmpty(userJson)) {
      mCurrentAccount = mCurrentAccount.fromJson(userJson);
    }
  }

  private void saveUserData() {
    if (mCurrentAccount != null) {
      prefs().save(USER_JSON_KEY, mCurrentAccount.toJson());
    }
  }

  private void clear() {
    mCurrentAccount = null;
    clearUserData();
  }

  private void clearUserData() {
    prefs().remove(USER_STORAGE);
  }

  private static Prefs prefs() {
    return Prefs.with(StarterKitApp.appContext(), USER_STORAGE, Context.MODE_PRIVATE);
  }

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    public static final AccountManager INSTANCE = new AccountManager();
  }

  public static AccountManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override public String provideToken() {
    return getCurrentAccount().token();
  }
}
