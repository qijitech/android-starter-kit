package starter.kit.account;

import android.content.Context;
import starter.kit.util.Strings;

public class FileAuthPreferences {

  private static final String KEY_USER = "user";
  private static final String KEY_TOKEN = "token";
  private static final String KEY_USER_DATA = "user_data";

  private AccountStoreHelper preferences;

  public FileAuthPreferences(Context context) {
    preferences = new AccountStoreHelper(context);
  }

  public void setUser(String user) {
    preferences.write(KEY_USER, user);
  }

  public void setToken(String token) {
    preferences.write(KEY_TOKEN, token);
  }

  public void setUserData(String userData) {
    preferences.write(KEY_USER_DATA, userData);
  }

  public String getUser() {
    return preferences.getString(KEY_USER);
  }

  public String getToken() {
    return preferences.getString(KEY_TOKEN);
  }

  public String getUserData() {
    return preferences.getString(KEY_USER_DATA);
  }

  public void clear() {
    preferences.clear();
  }

  public boolean isLogin() {
    return !Strings.isBlank(getToken());
  }
}
