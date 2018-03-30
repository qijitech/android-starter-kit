package starter.kit.util;

import android.support.annotation.NonNull;
import android.view.View;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import rx.subjects.BehaviorSubject;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public final class RxLibraryUtils {

  private RxLibraryUtils() {
  }

  public static <T> LifecycleTransformer<T> bindFragment() {
    return RxLifecycleAndroid.bindFragment(BehaviorSubject.create());
  }

  public static <T> LifecycleTransformer<T> bindActivity() {
    return RxLifecycleAndroid.bindActivity(BehaviorSubject.create());
  }

  public static <T> LifecycleTransformer<T> bindView(@NonNull final View view) {
    return RxLifecycleAndroid.bindView(view);
  }
}
