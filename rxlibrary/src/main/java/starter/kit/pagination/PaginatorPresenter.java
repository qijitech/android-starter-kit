package starter.kit.pagination;

import android.os.Bundle;

import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import nucleus5.presenter.Factory;
import nucleus5.view.OptionalView;
import starter.kit.app.StarterPresenter;
import starter.kit.app.StarterRecyclerFragment;
import starter.kit.util.RxUtils;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class PaginatorPresenter<T extends PaginatorContract> extends StarterPresenter<StarterRecyclerFragment> {

    private static final int RESTARTABLE_ID = 100;

    private PublishSubject<PaginatorEmitter> mRequests = PublishSubject.create();

    @SuppressWarnings("Unchecked")
    @Override
    protected void onCreate (Bundle savedState) {
        super.onCreate(savedState);

        restartableReplay(restartableId(), new Factory<Observable<T>>() {
            @Override
            public Observable<T> create () {
                return observableFactory();
            }
        }, new BiConsumer<StarterRecyclerFragment, T>() {
            @Override
            public void accept (@NonNull StarterRecyclerFragment fragment, @NonNull T items)
                    throws Exception {
                //noinspection unchecked
                fragment.onSuccess(items);
            }
        }, new BiConsumer<StarterRecyclerFragment, Throwable>() {
            @Override
            public void accept (@NonNull StarterRecyclerFragment fragment,
                                @NonNull Throwable throwable) throws Exception {
                fragment.onError(throwable);
            }
        });
    }

    private Observable<T> observableFactory () {
        return view().concatMap(new Function<OptionalView<StarterRecyclerFragment>, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply (
                    @NonNull OptionalView<StarterRecyclerFragment> fragment)
                    throws Exception {
                return mRequests.startWith(fragment.view.getPaginatorEmitter())
                        .concatMap(new Function<PaginatorEmitter, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply (@NonNull PaginatorEmitter emitter)
                                    throws Exception {
                                BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
                                return request(emitter.firstPaginatorKey(), emitter.nextPaginatorKey(), emitter.perPage())
                                        .subscribeOn(io())
                                        .compose(RxUtils.progressTransformer(fragment.view))
                                        .compose(RxLifecycleAndroid.bindFragment(lifecycle))
                                        .observeOn(mainThread());
                            }
                        });
            }
        });
    }

    public int restartableId () {
        return RESTARTABLE_ID;
    }

    public abstract Observable<T> request (String firstPaginatorKey, String nextPaginatorKey, int perPage);

    public void request () {
        start(restartableId());
    }

    public void requestNext (PaginatorEmitter paginator) {
        mRequests.onNext(paginator);
    }
}
