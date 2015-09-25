/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo;

import java.util.List;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface FeedService {

  @GET("api/v1/tweet/list") Call<Result<List<Tweet>>> getTweetList(
      @Query("page") String page,
      @Query("page_size") String pageSize);
}
