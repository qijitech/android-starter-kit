package starter.kit.retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * https://gist.github.com/joen93/7d4ae30ce29bef6127d74a26de151985
 */
public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
  private final RxJavaCallAdapterFactory original;

  private RxErrorHandlingCallAdapterFactory() {
    original = RxJavaCallAdapterFactory.create();
  }

  public static CallAdapter.Factory create() {
    return new RxErrorHandlingCallAdapterFactory();
  }

  @Override public CallAdapter<?, ?> get(final Type returnType, final Annotation[] annotations,
      final Retrofit retrofit) {
    return new RxCallAdapterWrapper<>(retrofit, original.get(returnType, annotations, retrofit));
  }

  private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Observable<R>> {
    private final Retrofit mRetrofit;
    private final CallAdapter<R, ?> mWrappedCallAdapter;

    public RxCallAdapterWrapper(final Retrofit retrofit, final CallAdapter<R, ?> wrapped) {
      mRetrofit = retrofit;
      mWrappedCallAdapter = wrapped;
    }

    @Override public Type responseType() {
      return mWrappedCallAdapter.responseType();
    }

    @SuppressWarnings("unchecked") @Override public Observable<R> adapt(Call<R> call) {
      return ((Observable) mWrappedCallAdapter.adapt(call)).onErrorResumeNext(
          new Func1<Throwable, Observable>() {
            @Override public Observable call(Throwable throwable) {
              return Observable.error(RxCallAdapterWrapper.this.asRetrofitException(throwable));
            }
          });
    }

    private RetrofitException asRetrofitException(final Throwable throwable) {
      // We had non-200 http error
      if (throwable instanceof HttpException) {
        final HttpException httpException = (HttpException) throwable;
        final Response response = httpException.response();

        return RetrofitException.httpError(response.raw().request().url().toString(), response,
            mRetrofit);
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
