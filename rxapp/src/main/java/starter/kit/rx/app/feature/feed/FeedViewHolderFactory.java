package starter.kit.rx.app.feature.feed;

import android.content.Context;
import android.view.ViewGroup;
import starter.kit.rx.app.model.entity.Feed;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyViewHolder;

public class FeedViewHolderFactory extends BaseEasyViewHolderFactory {

  private static final int VIEW_TYPE_TEXT = 0;
  private static final int VIEW_TYPE_IMAGE_SINGLE = 1;
  private static final int VIEW_TYPE_IMAGE_MULTI = 2;

  public FeedViewHolderFactory(Context context) {
    super(context);
  }

  @Override public EasyViewHolder create(int viewType, ViewGroup parent) {
    switch (viewType) {
      case VIEW_TYPE_TEXT:
        return new FeedsTextViewHolder(parent.getContext(), parent);
      case VIEW_TYPE_IMAGE_SINGLE:
        return new FeedsSingleImageViewHolder(parent.getContext(), parent);
      case VIEW_TYPE_IMAGE_MULTI:
        return new FeedsMultiImageViewHolder(parent.getContext(), parent);
    }
    return null;
  }

  @Override public int itemViewType(Object object) {
    if (object instanceof Feed) {
      Feed feed = (Feed) object;
      if (feed.images == null || feed.images.size() <= 0) {
        return VIEW_TYPE_TEXT;
      }
      if (feed.images.size() == 1) {
        return VIEW_TYPE_IMAGE_SINGLE;
      }
      return VIEW_TYPE_IMAGE_MULTI;
    }
    return -1;
  }
}
