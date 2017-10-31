package starter.kit.rx.app.network.service;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import starter.kit.pagination.LengthAwarePaginator;
import starter.kit.pagination.Paginator;
import starter.kit.rx.app.model.entity.Feed;

public interface FeedService {

  @GET("/posts/pages") Observable<LengthAwarePaginator<Feed>> fetchFeedsWithPage(
      @Query("page") String page, @Query("page_size") int pageSize, @Query("dump") String dump);

  @GET("/posts") Observable<LengthAwarePaginator<Feed>> fetchFeeds(@Query("max_id") String maxId,
      @Query("page_size") int pageSize, @Query("dump") String dump);

  @GET("/posts/paginator") Observable<Paginator<Feed>> paginator(@Query("page") String page,
      @Query("page_size") int pageSize);
}
