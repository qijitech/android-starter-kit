
<iframe height=498 width=510 src="http://player.youku.com/embed/XMTQ3ODc4NzM5Ng==" frameborder=0 allowfullscreen></iframe>


# 引入权限

```java
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```

# 编写自己的Application继承StarterKitApp

```java
public class YourApp extends StarterKitApp {

  @Override public void onCreate() {
    super.onCreate();
    // TODO your code
  }
}
```

# 编写xml布局文件list_item_tweet.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.smartydroid.android.starter.kit.widget.ForegroundLinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    android:background="#FFFFFF"
    android:orientation="vertical"
    >
  <TextView
      android:id="@+id/text_tweet_content"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:layout_marginLeft="@dimen/activity_horizontal_margin"
      android:layout_marginRight="@dimen/activity_horizontal_margin"
      android:layout_marginTop="8dp"
      android:maxLines="5"
      android:textColor="#636363"
      android:textSize="18sp"
      />

  <RelativeLayout
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:layout_marginLeft="@dimen/activity_horizontal_margin"
      android:layout_marginRight="@dimen/activity_horizontal_margin"
      android:layout_marginTop="4dp"
      android:paddingBottom="8dp"
      >

    <TextView
        android:id="@+id/text_tweet_source"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentLeft="true"
        android:singleLine="true"
        android:textColor="#919191"
        android:textSize="14sp"
        />

    <TextView
        android:id="@+id/text_tweet_published_time"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:layout_marginLeft="16dp"
        android:singleLine="true"
        android:text="12:48"
        android:textColor="#919191"
        android:textSize="14sp"
        />
  </RelativeLayout>
</com.smartydroid.android.starter.kit.widget.ForegroundLinearLayout>
```

# 自定义ViewHolder，继承EasyViewHolder


```java
public class TweetViewHolder extends EasyViewHolder<Tweet> {

  @Bind(R.id.text_tweet_content) TextView mTweetContent;
  @Bind(R.id.text_tweet_published_time) TextView mTweetPublishAt;
  @Bind(R.id.text_tweet_source) TextView mTweetSource;

  public TweetViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_tweet);
    ButterKnife.bind(this, itemView);
  }

  @Override public void bindTo(Tweet tweet) {
    mTweetContent.setText(tweet.content);
    mTweetSource.setText(tweet.source);
    mTweetPublishAt.setText(String.valueOf(tweet.publishedAt));
  }
}
```

# Retrofit Service

```java
public interface TweetService {

  @GET("api/v1/tweet/list") Call<Collection<Tweet>> getTweetList(
      @Query("page") int page,
      @Query("page_size") int pageSize);
}
```

# Retrofit Client

```java
public class StarterNetwork {

  private static final String sBaseUrl = "http://duanzi.net/";
  private Retrofit mRetrofit;

  // Make this class a thread safe singleton
  private static class SingletonHolder {
    private static final StarterNetwork INSTANCE = new StarterNetwork();
  }

  public static synchronized StarterNetwork get() {
    return SingletonHolder.INSTANCE;
  }


  protected Retrofit.Builder newRetrofitBuilder() {
    return new Retrofit.Builder();
  }

  private Retrofit retrofit() {
    if (mRetrofit == null) {
      Retrofit.Builder builder = newRetrofitBuilder();
      mRetrofit = builder.baseUrl(sBaseUrl)
          .addConverterFactory(JacksonConverterFactory.create())
          .build();
    }

    return mRetrofit;
  }

  public static TweetService createTweetService() {
    return get().retrofit().create(TweetService.class);
  }
}
```

# 编写Fragment继承PagesRecyclerViewFragment

```java
public class FeedFragment extends PagesRecyclerViewFragment<Tweet> {

  private TweetService mTweetService;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTweetService = StarterNetwork.createTweetService();
  }

  @Override public void bindViewHolders(EasyRecyclerAdapter adapter) {
    adapter.bind(Tweet.class, TweetViewHolder.class);
  }

  @Override public Call<Collection<Tweet>> paginate(int page, int perPage) {
    return mTweetService.getTweetList(page, perPage);
  }

  @Override public Object getKeyForData(Tweet item) {
    return item.id;
  }
}
```

# Gradle文件

```java
	compile 'com.smartydroid:android-starter-kit:0.0.4'
```

# 第三方库

* appcompat-v7 - 
* recyclerview - 
* ButterKnife - http://jakewharton.github.io/butterknife
* Retrofit - http://square.github.io/retrofit
* ConverterJackson - http://square.github.io/retrofit
* EasyRecyclerAdapters - https://github.com/cmc00022/easyrecycleradapters


