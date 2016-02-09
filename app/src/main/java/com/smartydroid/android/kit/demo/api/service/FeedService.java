/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.api.service;

import com.smartydroid.android.kit.demo.model.entity.Feed;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FeedService {

  @GET("/feedsWithPage") Call<ArrayList<Feed>> getFeedList(
      @Query("page") int page,
      @Query("page_size") int pageSize);
}
