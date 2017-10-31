package starter.kit.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat.EditorCompat;
import support.ui.utilities.Strings;

/**
 * 用户信息SP类
 */
public class AuthPreferences {

  private static final String KEY_USER = "user";
  private static final String KEY_TOKEN = "token";
  private static final String KEY_USER_DATA = "user_data";

  private SharedPreferences preferences;

  public AuthPreferences(Context context) {
    preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
  }

  public String getUser() {
    return preferences.getString(KEY_USER, null);
  }

  public void setUser(String user) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(KEY_USER, user);
    EditorCompat.getInstance().apply(editor);
  }

  public String getToken() {
    return preferences.getString(KEY_TOKEN, null);
  }

  public void setToken(String token) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(KEY_TOKEN, token);
    EditorCompat.getInstance().apply(editor);
  }

  public String getUserData() {
    return preferences.getString(KEY_USER_DATA, null);
  }

  public void setUserData(String userData) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(KEY_USER_DATA, userData);
    EditorCompat.getInstance().apply(editor);
  }

  public void clear() {
    SharedPreferences.Editor editor = preferences.edit().clear();
    EditorCompat.getInstance().apply(editor);
  }

  public boolean isLogin() {
    return !Strings.isBlank(getToken());
  }
}
