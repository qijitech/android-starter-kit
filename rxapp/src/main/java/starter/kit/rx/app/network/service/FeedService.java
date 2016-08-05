package starter.kit.rx.app.network.service;

import java.util.ArrayList;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import starter.kit.rx.app.model.entity.Feed;

public interface FeedService {

  @GET("/posts/pages") Observable<ArrayList<Feed>> fetchFeedsWithPage(
      @Query("page") String page,
      @Query("page_size") int pageSize);

  @GET("/posts") Observable<ArrayList<Feed>> fetchFeeds(
      @Query("since_id") String sinceId,
      @Query("max_id") String maxId,
      @Query("page_size") int pageSize);
}
