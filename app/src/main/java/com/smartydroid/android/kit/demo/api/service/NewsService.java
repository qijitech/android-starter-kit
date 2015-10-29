/**
 * Created by YuGang Yang on September 25, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.smartydroid.android.kit.demo.api.service;

import com.smartydroid.android.kit.demo.model.entity.News;
import java.util.ArrayList;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface NewsService {

  @GET("/news") Call<ArrayList<News>> getNewsList(
      @Query("page") int page,
      @Query("page_size") int pageSize,
      @Query("category_id") int categoryId);
}
