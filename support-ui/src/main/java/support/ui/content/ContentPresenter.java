package support.ui.content;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import java.lang.reflect.Constructor;
import support.ui.R;
import support.ui.utilities.LayoutHelper;

public final class ContentPresenter {

  private static final int ID_NONE = -1;
  private static final int LoadViewId = R.id.support_ui_load_view;
  private static final int EmptyViewId = R.id.support_ui_empty_view;
  private static final int ContentViewId = R.id.support_ui_content_view;

  private SparseArrayCompat<Class<View>> mViewClassArray = new SparseArrayCompat<>(3);
  private SparseArrayCompat<View> mViewArray = new SparseArrayCompat<>(3);
  int mCurrentId = ID_NONE;
  ViewGroup mContainer;
  Context mContext;

  private EasyRecyclerAdapter mAdapter;

  public ContentPresenter(Class<View> loadViewClass, Class<View> emptyViewClass,
      Class<View> contentViewClass) {
    buildViewClassArray(loadViewClass, emptyViewClass, contentViewClass);
  }

  public ContentPresenter onCreate(Context context) {
    mContext = context;
    mAdapter = new EasyRecyclerAdapter(context);
    return this;
  }

  public ContentPresenter onAttachView(ViewGroup container) {
    mContainer = container;
    return this;
  }

  public void onDestroyView() {
    mCurrentId = ID_NONE;
    mViewArray.clear();
  }

  public void onDestroy() {
    mContext = null;
    mContainer = null;
    mViewClassArray = null;
    mViewArray = null;
  }

  /**
   * 显示进度条
   */
  public ContentPresenter displayLoadView() {
    final int loadViewId = LoadViewId;
    if (mCurrentId != loadViewId) {
      displayView(loadViewId);
    }
    return this;
  }

  /**
   * 显示空白页
   */
  public ContentPresenter displayEmptyView() {
    final int emptyViewId = EmptyViewId;
    if (mCurrentId != emptyViewId) {
      displayView(emptyViewId);
    }
    return this;
  }

  /**
   * 显示内容
   */
  public ContentPresenter displayContentView() {
    final int contentViewId = ContentViewId;
    if (mCurrentId != contentViewId) {
      displayView(contentViewId);
    }
    return this;
  }

  public ContentPresenter buildImageView(@DrawableRes int drawableRes) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.buildImageView(drawableRes);
    }
    return this;
  }

  public ContentPresenter buildEmptyTitle(@StringRes int stringRes) {
    return buildEmptyTitle(mContext.getResources().getString(stringRes));
  }

  public ContentPresenter buildEmptyTitle(String title) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.buildTitle(title);
    }
    return this;
  }

  public ContentPresenter buildEmptySubtitle(@StringRes int stringRes) {
    return buildEmptySubtitle(mContext.getResources().getString(stringRes));
  }

  public ContentPresenter buildEmptySubtitle(String subtitle) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.buildSubtitle(subtitle);
    }
    return this;
  }

  private View displayView(@IdRes int viewId) {
    final ViewGroup container = mContainer;
    container.removeAllViews();
    final View view = checkView(viewId);
    final ViewGroup.LayoutParams layoutParams = LayoutHelper.createViewGroupLayoutParams();
    container.addView(view, layoutParams);
    mCurrentId = viewId;
    return view;
  }

  @Nullable private View checkView(int viewId) {
    final SparseArrayCompat<View> viewArray = mViewArray;
    View view = viewArray.get(viewId);
    if (view == null) {
      view = buildView(viewId);
      viewArray.put(viewId, view);
    }
    return view;
  }

  private ContentPresenter buildViewClassArray(Class<View> loadViewClass,
      Class<View> emptyViewClass, Class<View> contentViewClass) {
    final SparseArrayCompat<Class<View>> viewClassArray = mViewClassArray;
    viewClassArray.put(LoadViewId, loadViewClass);
    viewClassArray.put(EmptyViewId, emptyViewClass);
    viewClassArray.put(ContentViewId, contentViewClass);
    return this;
  }

  @Nullable private View buildView(int viewId) {
    final SparseArrayCompat<Class<View>> viewClassArray = mViewClassArray;
    final Class<View> viewClass = viewClassArray.get(viewId);
    try {
      Constructor<? extends View> constructor = viewClass.getDeclaredConstructor(Context.class);
      final Context context = mContext;
      return constructor.newInstance(context);
    } catch (Throwable e) {
      throw new RuntimeException(
          "Unable to create View for" + viewClass + ". " + e.getCause().getMessage(), e);
    }
  }
}
