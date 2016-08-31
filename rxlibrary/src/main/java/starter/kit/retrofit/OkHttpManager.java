package starter.kit.retrofit;

import com.orhanobut.logger.Logger;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import starter.kit.account.AccountManager;

public final class OkHttpManager {

  private OkHttpClient mOkHttpClient;

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    private static final OkHttpManager INSTANCE = new OkHttpManager();
  }

  public static synchronized OkHttpManager get() {
    return SingletonHolder.INSTANCE;
  }

  private OkHttpManager() {

  }

  public OkHttpClient okHttpClient(String accept) {
    if (mOkHttpClient == null) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      HttpLoggingInterceptor loggingInterceptor =
          new HttpLoggingInterceptor((message) -> Logger.d(message));
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addNetworkInterceptor(loggingInterceptor);
      builder.addInterceptor(new DefaultHeaderInterceptor(AccountManager.INSTANCE.token(), accept));
      mOkHttpClient = builder.build();
    }
    return mOkHttpClient;
  }
}
