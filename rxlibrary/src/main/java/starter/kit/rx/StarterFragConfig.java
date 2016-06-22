package starter.kit.rx;

import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import rx.Observable;
import starter.kit.model.Entity;
import starter.kit.util.Maps;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyViewHolder;

public class StarterFragConfig<E extends Entity> {

  private Observable<ArrayList<E>> resourceObservable;
  private BaseEasyViewHolderFactory viewHolderFactory;
  private HashMap<Class, Class<? extends EasyViewHolder>> boundViewHolders;

  private RecyclerView.LayoutManager layoutManager;
  private RecyclerView.ItemDecoration decor;
  private RecyclerView.ItemAnimator animator;

  private int[] colorSchemeColors;
  private boolean enabled;

  public Observable<ArrayList<E>> getResourceObservable() {
    return resourceObservable;
  }

  public BaseEasyViewHolderFactory getViewHolderFactory() {
    return viewHolderFactory;
  }

  public HashMap<Class, Class<? extends EasyViewHolder>> getBoundViewHolders() {
    return boundViewHolders;
  }

  public RecyclerView.LayoutManager getLayoutManager() {
    return layoutManager;
  }

  public RecyclerView.ItemDecoration getDecor() {
    return decor;
  }

  public RecyclerView.ItemAnimator getAnimator() {
    return animator;
  }

  public int[] getColorSchemeColors() {
    return colorSchemeColors;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public static class Builder<E extends Entity> {
    private Observable<ArrayList<E>> resourceObservable;
    private BaseEasyViewHolderFactory viewHolderFactory;
    private HashMap<Class, Class<? extends EasyViewHolder>> boundViewHolders = Maps.newHashMap();

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decor;
    private RecyclerView.ItemAnimator animator;

    private int[] colorSchemeColors;
    private boolean enabled = true;

    public StarterFragConfig build() {
      StarterFragConfig config = new StarterFragConfig();
      config.resourceObservable = resourceObservable;
      config.viewHolderFactory = viewHolderFactory;
      config.boundViewHolders = boundViewHolders;
      config.layoutManager = layoutManager;
      config.decor = decor;
      config.animator = animator;
      config.colorSchemeColors = colorSchemeColors;
      config.enabled = enabled;
      return config;
    }

    public Builder observable(Observable<ArrayList<E>> observable) {
      resourceObservable = observable;
      return this;
    }

    public Builder viewHolderFactory(BaseEasyViewHolderFactory viewHolderFactory) {
      this.viewHolderFactory = viewHolderFactory;
      return this;
    }

    public Builder bind(Class valueClass, Class<? extends EasyViewHolder> viewHolder) {
      boundViewHolders.put(valueClass, viewHolder);
      return this;
    }

    public Builder recyclerLayoutManager(RecyclerView.LayoutManager layoutManager) {
      this.layoutManager = layoutManager;
      return this;
    }

    public Builder recyclerViewDecor(RecyclerView.ItemDecoration decor) {
      this.decor = decor;
      return this;
    }

    public Builder recyclerViewAnimator(RecyclerView.ItemAnimator animator) {
      this.animator = animator;
      return this;
    }

    public Builder swipeRefreshLayoutColors(@ColorInt int... colors) {
      this.colorSchemeColors = colors;
      return this;
    }

    public Builder swipeRefreshLayoutEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }
  }
}
