package starter.kit.app;

import android.os.Bundle;
import android.view.View;
import java.net.UnknownHostException;
import mehdi.sakout.dynamicbox.DynamicBox;
import nucleus.presenter.Presenter;
import rx.functions.Action0;
import starter.kit.retrofit.ErrorResponse;
import starter.kit.util.ErrorHandler;
import starter.kit.util.NetworkContract;
import starter.kit.util.RxUtils;
import support.ui.content.DefaultEmptyView;

import static starter.kit.util.Utilities.isNotNull;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class StarterNetworkFragment<P extends Presenter> extends StarterFragment<P>
    implements NetworkContract.ContentInterface, View.OnClickListener {

  private DynamicBox mDynamicBox;
  private StarterFragConfig mFragConfig;

  // Default Tags
  private final String TAG_INTERNET_OFF 	 =  "INTERNET_OFF";
  private final String TAG_LOADING_CONTENT =  "LOADING_CONTENT";
  private final String TAG_OTHER_EXCEPTION =  "OTHER_EXCEPTION";
  private final String TAG_EMPTY =  "TAG_EMPTY";

  private DefaultEmptyView mEmptyView;

  protected void buildFragConfig(StarterFragConfig fragConfig) {
    mFragConfig = fragConfig;
  }

  public DefaultEmptyView getEmptyView() {
    return mEmptyView;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mDynamicBox = new DynamicBox(getContext(), targetView());
    mDynamicBox.setClickListener(this);
    mEmptyView = new DefaultEmptyView(getContext());
    mDynamicBox.addCustomView(mEmptyView, TAG_EMPTY);

    if (mFragConfig != null) {
      if (mFragConfig.getCustomExceptionView() != null) {
        mDynamicBox.addCustomView(mFragConfig.getCustomExceptionView(), TAG_OTHER_EXCEPTION);
      }

      if (mFragConfig.getCustomEmptyView() != null) {
        mDynamicBox.addCustomView(mFragConfig.getCustomEmptyView(), TAG_EMPTY);
      }

      if (mFragConfig.getCustomInternetView() != null) {
        mDynamicBox.addCustomView(mFragConfig.getCustomInternetView(), TAG_INTERNET_OFF);
      }

      if (mFragConfig.getCustomLoadingView() != null) {
        mDynamicBox.addCustomView(mFragConfig.getCustomLoadingView(), TAG_LOADING_CONTENT);
      }
    }
  }

  @Override public void onDestroyView() {
    mDynamicBox.setClickListener(null);
    mDynamicBox = null;
    mEmptyView = null;
    super.onDestroyView();
  }

  public DynamicBox getDynamicBox() {
    return mDynamicBox;
  }

  public StarterFragConfig getFragConfig() {
    return mFragConfig;
  }

  /**
   * refers to the target view, eg a ListView or a layout
   *
   * @return View
   */
  public View targetView() {
    return getView();
  }

  public void showLoadingView() {
    showDynamicBox(TAG_LOADING_CONTENT);
  }

  public void showContentView() {
    RxUtils.empty(new Action0() {
      @Override public void call() {
        mDynamicBox.hideAll();
      }
    });
  }

  public void showEmptyView() {
    showDynamicBox(TAG_EMPTY);
  }

  public void showInternetView() {
    showDynamicBox(TAG_INTERNET_OFF);
  }

  public void showExceptionView() {
    showDynamicBox(TAG_OTHER_EXCEPTION);
  }

  public void showDynamicBox(String tag) {
    RxUtils.empty(new Action0() {
      @Override public void call() {
        if (isNotNull(mDynamicBox)) {
          mDynamicBox.showCustomView(tag);
        }
      }
    });
  }

  @Override public void showProgress() {
    showLoadingView();
  }

  @Override public void hideProgress() {

  }

  @Override public void onError(Throwable throwable) {
    ErrorResponse errorResponse = ErrorHandler.handleThrowable(throwable);
    if (throwable.getCause() instanceof UnknownHostException) {
      showInternetView();
    } else {
      showExceptionView();
    }
  }

  @Override public void onSuccess(Object data) {
    showContentView();
  }

  @Override public void onClick(View view) {

  }
}
