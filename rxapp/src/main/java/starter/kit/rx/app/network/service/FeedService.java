package starter.kit.rx.app.network.service;

import java.util.ArrayList;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import starter.kit.rx.app.model.entity.Feed;

public interface FeedService {

  @GET("/posts") Observable<ArrayList<Feed>> fetchFeeds(
      @Query("page") int page,
      @Query("page_size") int pageSize);
}
