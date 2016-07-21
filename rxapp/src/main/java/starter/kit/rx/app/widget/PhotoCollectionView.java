package starter.kit.rx.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.ArrayList;
import starter.kit.rx.app.R;
import starter.kit.rx.app.model.entity.Image;

public class PhotoCollectionView extends RecyclerView {

  private SimpleAdapter mAdapter;

  public PhotoCollectionView(Context context) {
    this(context, null);
  }

  public PhotoCollectionView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PhotoCollectionView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize();
  }

  private void initialize() {
    setNestedScrollingEnabled(false);
    setLayoutManager(new GridLayoutManager(getContext(), 3));
    addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(Color.TRANSPARENT).size(20).build());
    mAdapter = new SimpleAdapter();
    setAdapter(mAdapter);
  }

  public void setData(ArrayList<Image> data) {
    mAdapter.setData(data);
  }

  private class SimpleAdapter extends Adapter<ViewHolder> {
    private static final int ITEM_TYPE_IMAGE_SINGLE = 0;
    private static final int ITEM_TYPE_IMAGE_MULTI = 1;

    private ArrayList<Image> mData;

    public SimpleAdapter() {
      mData = new ArrayList<>();
    }

    public void setData(ArrayList<Image> data) {
      mData = data;
      notifyDataSetChanged();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == ITEM_TYPE_IMAGE_SINGLE) {
        return SingleImageViewHolder.create(getContext(), parent);
      }
      if (viewType == ITEM_TYPE_IMAGE_MULTI) {
        return MultiImageViewHolder.create(getContext(), parent);
      }
      return null;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      Image image = mData.get(position);
      if (holder instanceof SingleImageViewHolder) {
        SingleImageViewHolder viewHolder = (SingleImageViewHolder) holder;
        viewHolder.bind(image);
        return;
      }
      if (holder instanceof MultiImageViewHolder) {
        MultiImageViewHolder viewHolder = (MultiImageViewHolder) holder;
        viewHolder.bind(image);
      }
    }

    @Override public int getItemCount() {
      return mData.size();
    }

    @Override public int getItemViewType(int position) {
      if (mData.size() > 1) {
        return ITEM_TYPE_IMAGE_MULTI;
      }
      return ITEM_TYPE_IMAGE_SINGLE;
    }
  }

  static class SingleImageViewHolder extends ViewHolder {
    public Image image;
    @Bind(R.id.image_feed_thumbnail) SimpleDraweeView mThumbnailView;

    static SingleImageViewHolder create(Context context, ViewGroup parent) {
      return new SingleImageViewHolder(context, parent);
    }

    private SingleImageViewHolder(Context context, ViewGroup parent) {
      super(LayoutInflater.from(context).inflate(R.layout.list_item_image_single, parent, false));
      ButterKnife.bind(this, itemView);
    }

    public void bind(Image image) {
      this.image = image;
      mThumbnailView.setImageURI(image.uri());
    }
  }

  static class MultiImageViewHolder extends ViewHolder {
    public Image image;
    @Bind(R.id.image_feed_thumbnail) SimpleDraweeView mThumbnailView;

    static MultiImageViewHolder create(Context context, ViewGroup parent) {
      return new MultiImageViewHolder(context, parent);
    }

    private MultiImageViewHolder(Context context, ViewGroup parent) {
      super(LayoutInflater.from(context).inflate(R.layout.list_item_image_multi, parent, false));
      ButterKnife.bind(this, itemView);
    }
    public void bind(Image image) {
      this.image = image;
      mThumbnailView.setImageURI(image.uri());
    }
  }
}
