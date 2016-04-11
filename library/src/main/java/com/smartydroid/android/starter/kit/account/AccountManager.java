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

  private static final String PREFS_ACCOUNT_STORAGE = "accounts";
  private static final String PREFS_KEY_ACCOUNT_JSON = "accounts_json";

  private ArrayList<CurrentAccountObserver> mObservers;
  private Account mCurrentAccount;

  public interface CurrentAccountObserver {
    void notifyChange();
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

  @SuppressWarnings("unchecked") public static <T extends Account> T getCurrentAccount() {
    return (T) getInstance().mCurrentAccount;
  }

  public static boolean isLogin() {
    return getCurrentAccount() != null;
  }

  public static void setCurrentAccount(Account account) {
    if (getInstance().mCurrentAccount == account) {
      return;
    }
    getInstance().mCurrentAccount = account;
  }

  public static void store() {
    getInstance().saveAccountData();
  }

  /**
   * 保存用户
   */
  public static void store(Account account) {
    setCurrentAccount(account);
    getInstance().saveAccountData();
  }

  public static void logout() {
    getInstance().clear();
    notifyDataChanged();
  }

  private AccountManager() {
    loadAccountDataFromPrefs();
  }

  private void loadAccountDataFromPrefs() {
    String accountJson = prefs().getString(PREFS_KEY_ACCOUNT_JSON, null);
    if (!TextUtils.isEmpty(accountJson)) {
      mCurrentAccount = ((StarterKitApp)StarterKitApp.getInstance()).accountFromJson(accountJson);
    }
  }

  private void saveAccountData() {
    if (mCurrentAccount != null) {
      prefs().save(PREFS_KEY_ACCOUNT_JSON, mCurrentAccount.toJson());
    }
  }

  private void clear() {
    mCurrentAccount = null;
    clearAccountData();
  }

  private void clearAccountData() {
    prefs().remove(PREFS_ACCOUNT_STORAGE);
  }

  private static Prefs prefs() {
    return Prefs.with(StarterKitApp.appContext(), PREFS_ACCOUNT_STORAGE, Context.MODE_PRIVATE);
  }

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    public static final AccountManager INSTANCE = new AccountManager();
  }

  public static AccountManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override public String provideToken() {
    if (isLogin()) {
      return getCurrentAccount().token();
    }

    return null;
  }
}
