package starter.kit.retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import starter.kit.util.Preconditions;

public final class Network {

  private String baseUrl;
  private Retrofit mRetrofit;

  private OkHttpClient client;

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    private static final Network INSTANCE = new Network();
  }

  public static synchronized Network get() {
    return SingletonHolder.INSTANCE;
  }

  private Network() {
  }

  public Retrofit retrofit() {
    Preconditions.checkNotNull(baseUrl, "Base URL required.");
    if (mRetrofit == null) {
      mRetrofit = newRetrofitBuilder()
          .baseUrl(baseUrl)
          .client(client)
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();
    }
    return mRetrofit;
  }

  protected Retrofit.Builder newRetrofitBuilder() {
    return new Retrofit.Builder();
  }

  public static class Builder {
    private String baseUrl;
    private String accept;
    private OkHttpClient mClient;

    public Network build() {
      Preconditions.checkNotNull(baseUrl, "Base URL required.");
      ensureSaneDefaults();

      Network network = get();
      network.baseUrl = baseUrl;
      network.client = mClient;

      return network;
    }

    private void ensureSaneDefaults() {
      if (mClient == null) {
        mClient = OkHttpManager.get().okHttpClient();
      }
    }

    public Builder client(OkHttpClient client) {
      mClient = client;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      Preconditions.checkNotNull(baseUrl, "baseUrl == null");
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder accept(String accept) {
      Preconditions.checkNotNull(accept, "accept == null");
      this.accept = accept;
      return this;
    }
  }
}
