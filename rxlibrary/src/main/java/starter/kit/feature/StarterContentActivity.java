package starter.kit.feature;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import nucleus.presenter.Presenter;
import starter.kit.feature.rx.RxStarterActivity;
import support.ui.content.ContentPresenter;
import support.ui.content.EmptyView;
import support.ui.content.ErrorView;
import support.ui.content.ReflectionContentPresenterFactory;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public abstract class StarterContentActivity<P extends Presenter> extends RxStarterActivity<P>
    implements EmptyView.OnEmptyViewClickListener, ErrorView.OnErrorViewClickListener {

  private ReflectionContentPresenterFactory factory =
      ReflectionContentPresenterFactory.fromViewClass(getClass());
  private ContentPresenter contentPresenter;

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    contentPresenter = factory.createContentPresenter();
    contentPresenter.onCreate(this);

    contentPresenter.attachContainer(provideContainer((ViewGroup) getWindow().getDecorView()));
    contentPresenter.attachContentView(provideContentView());

    contentPresenter.setOnEmptyViewClickListener(this);
    contentPresenter.setOnErrorViewClickListener(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    contentPresenter.onDestroyView();
    contentPresenter.onDestroy();
    contentPresenter = null;
  }

  public ContentPresenter getContentPresenter() {
    return contentPresenter;
  }

  public ViewGroup provideContainer(ViewGroup view) {
    return view;
  }

  public abstract View provideContentView();
}
