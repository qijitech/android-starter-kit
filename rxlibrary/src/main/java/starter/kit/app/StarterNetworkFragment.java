package starter.kit.app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import nucleus.presenter.Presenter;
import starter.kit.retrofit.ErrorResponse;
import starter.kit.util.ErrorHandler;
import starter.kit.util.NetworkContract;
import starter.kit.util.RxUtils;
import support.ui.content.ContentPresenter;
import support.ui.content.EmptyView;
import support.ui.content.ErrorView;
import support.ui.content.ReflectionContentPresenterFactory;
import support.ui.content.RequiresContent;

import static starter.kit.util.Utilities.isNotNull;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
@RequiresContent public abstract class StarterNetworkFragment<T, P extends Presenter>
    extends StarterFragment<P>
    implements NetworkContract.ContentInterface<T>,
    EmptyView.OnEmptyViewClickListener,
    ErrorView.OnErrorViewClickListener {

  private ReflectionContentPresenterFactory factory =
      ReflectionContentPresenterFactory.fromViewClass(getClass());
  private ContentPresenter contentPresenter;

  private StarterFragConfig mFragConfig;

  private ErrorResponse mErrorResponse;

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    contentPresenter = factory.createContentPresenter();
    contentPresenter.onCreate(getContext());

    contentPresenter.setOnEmptyViewClickListener(this);
    contentPresenter.setOnErrorViewClickListener(this);
  }

  protected void buildFragConfig(StarterFragConfig fragConfig) {
    mFragConfig = fragConfig;
  }

  @Override public void onResume() {
    if (isNotNull(contentPresenter)) {
      contentPresenter.attachContainer(provideContainer());
      contentPresenter.attachContentView(provideContentView());
    }
    super.onResume();
  }

  @Override public void onPause() {
    if (isNotNull(contentPresenter)) {
      contentPresenter.onDestroyView();
    }
    super.onPause();
  }

  @Override public void onDestroy() {
    contentPresenter.onDestroy();
    contentPresenter = null;

    super.onDestroy();
  }

  public ContentPresenter getContentPresenter() {
    return contentPresenter;
  }

  public StarterFragConfig getFragConfig() {
    return mFragConfig;
  }

  @Override public void showProgress() {
    if (mFragConfig.shouldDisplayLoadingView()) {
      RxUtils.empty(() -> getContentPresenter().displayLoadView());
    }
  }

  @Override public void hideProgress() {

  }

  @Override public void onError(Throwable throwable) {
    mErrorResponse = ErrorHandler.handleThrowable(throwable);
    if (mErrorResponse != null) {
      getContentPresenter().buildEmptyTitle(mErrorResponse.getMessage());
    }
  }

  @Override public void onSuccess(T data) {
  }

  public ViewGroup provideContainer() {
    return (ViewGroup) getView();
  }

  public abstract View provideContentView();

  public ErrorResponse getErrorResponse() {
    return mErrorResponse;
  }

  public void setErrorResponse(ErrorResponse errorResponse) {
    mErrorResponse = errorResponse;
  }
}
