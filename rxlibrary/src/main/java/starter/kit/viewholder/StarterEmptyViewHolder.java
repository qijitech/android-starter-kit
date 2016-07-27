package starter.kit.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import starter.kit.model.EmptyEntity;
import starter.kit.rx.R;
import support.ui.adapters.EasyViewHolder;

public class StarterEmptyViewHolder extends EasyViewHolder<EmptyEntity> {

  public StarterEmptyViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.starter_empty_view);
    ButterKnife.bind(this, itemView);
  }

  @Override public void bindTo(int position, EmptyEntity value) {

  }
}
