package starter.kit.retrofit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

  private final RxJava2CallAdapterFactory original;

  private RxErrorHandlingCallAdapterFactory() {
    original = RxJava2CallAdapterFactory.create();
  }

  public static CallAdapter.Factory create() {
    return new RxErrorHandlingCallAdapterFactory();
  }

  @Override
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    //noinspection unchecked
    return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
  }

  private static class RxCallAdapterWrapper<R> implements CallAdapter<Observable<R>, Object> {

    private final Retrofit retrofit;
    private final CallAdapter<Observable<R>, ?> wrapped;

    RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<Observable<R>, ?> callAdapter) {
      this.retrofit = retrofit;
      this.wrapped = callAdapter;
    }

    @Override public Type responseType() {
      return wrapped.responseType();
    }

    @Override public Object adapt(Call<Observable<R>> call) {
      //noinspection unchecked
      return ((Observable) wrapped.adapt(call)).onErrorResumeNext(
          new Function<Throwable, ObservableSource>() {
            @Override public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
              return Observable.error(
                  RxErrorHandlingCallAdapterFactory.RxCallAdapterWrapper.this.asRetrofitException(
                      throwable));
            }
          });
    }

    private RetrofitException asRetrofitException(Throwable throwable) {
      // We had non-200 http error
      if (throwable instanceof HttpException) {
        HttpException httpException = (HttpException) throwable;
        Response response = httpException.response();
        return RetrofitException.httpError(response.raw().request().url().toString(), response,
            retrofit);
      }
      // A network error happened
      if (throwable instanceof IOException) {
        return RetrofitException.networkError((IOException) throwable);
      }

      // We don't know what happened. We need to simply convert to an unknown error
      return RetrofitException.unexpectedError(throwable);
    }
  }
}
